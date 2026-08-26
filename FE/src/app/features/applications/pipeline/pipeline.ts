import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { ApplicationResponse, ApplicationStage, PipelineResponse } from '../../../core/models/api.models';
import { ApplicationService } from '../../../core/services/application.service';

@Component({
  selector: 'app-pipeline',
  templateUrl: './pipeline.component.html',
  styleUrl: './pipeline.component.css'
})
export class PipelineComponent implements OnInit {

  postingId!: string;
  pipeline: PipelineResponse[] = [];

  // Pre-mapped lookup dictionary to prevent template re-computations
  applicationsByStage: Map<ApplicationStage, ApplicationResponse[]> = new Map();

  stages: ApplicationStage[] = [
    ApplicationStage.APPLIED,
    ApplicationStage.SCREENING,
    ApplicationStage.INTERVIEW,
    ApplicationStage.OFFER,
    ApplicationStage.DECISION
  ];

  loading = false;
  errorMessage = '';

  constructor(
    private applicationService: ApplicationService,
    private route: ActivatedRoute
  ) { }

  ngOnInit(): void {
    this.postingId = this.route.snapshot.paramMap.get('postingId')!;
    this.loadPipeline();
  }

  loadPipeline(): void {
    this.loading = true;
    this.errorMessage = '';

    this.applicationService
      .getPipeline(this.postingId)
      .subscribe({
        next: (data) => {
          this.pipeline = data;
          this.buildStageMap();
          this.loading = false;
        },
        error: (error) => {
          console.error('Failed to load pipeline:', error);
          this.errorMessage = 'Failed to load pipeline.';
          this.loading = false;
        }
      });
  }

  private buildStageMap(): void {
    this.applicationsByStage.clear();

    // Pre-populate empty lists for all defined stages
    this.stages.forEach(stage => this.applicationsByStage.set(stage, []));

    // Fill with actual data returned from API
    this.pipeline.forEach(column => {
      this.applicationsByStage.set(column.stage, column.applications ?? []);
    });
  }

  getApplications(stage: ApplicationStage): ApplicationResponse[] {
    return this.applicationsByStage.get(stage) ?? [];
  }

  updateStage(
    application: ApplicationResponse,
    event: Event
  ): void {
    const select = event.target as HTMLSelectElement;
    const newStage = select.value as ApplicationStage;
    const previousStage = application.stage;

    if (previousStage === newStage) {
      return;
    }

    // 1. Optimistic Update (Immediate Feedback)
    application.stage = newStage;
    this.moveApplicationInMap(application, previousStage, newStage);

    // 2. Sync with Backend
    this.applicationService
      .updateApplicationStage(application.id, newStage)
      .subscribe({
        error: (error) => {
          console.error('Failed to update application stage:', error);
          this.errorMessage = 'Failed to update application stage.';

          // Revert back on failure
          application.stage = previousStage;
          select.value = previousStage;
          this.moveApplicationInMap(application, newStage, previousStage);
        }
      });
  }

  private moveApplicationInMap(
    app: ApplicationResponse,
    fromStage: ApplicationStage,
    toStage: ApplicationStage
  ): void {
    const sourceList = this.applicationsByStage.get(fromStage) || [];
    const targetList = this.applicationsByStage.get(toStage) || [];

    this.applicationsByStage.set(
      fromStage,
      sourceList.filter(item => item.id !== app.id)
    );
    this.applicationsByStage.set(
      toStage,
      [...targetList, app]
    );
  }
}