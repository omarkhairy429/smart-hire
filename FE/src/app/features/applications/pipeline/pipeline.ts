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
    this.postingId =
      this.route.snapshot.paramMap.get('postingId')!;

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
          this.loading = false;
        },
        error: (error) => {
          console.error('Failed to load pipeline:', error);
          this.errorMessage = 'Failed to load pipeline.';
          this.loading = false;
        }
      });
  }

  getApplications(stage: ApplicationStage): ApplicationResponse[] {
    return this.pipeline.find(
      column => column.stage === stage
    )?.applications ?? [];
  }

  updateStage(
    application: ApplicationResponse,
    event: Event
  ): void {

    const select = event.target as HTMLSelectElement;

    const newStage =
      select.value as ApplicationStage;

    if (application.stage === newStage) {
      return;
    }

    this.applicationService
      .updateApplicationStage(
        application.id,
        newStage
      )
      .subscribe({
        next: () => {
          this.loadPipeline();
        },
        error: (error) => {
          console.error(
            'Failed to update application stage:',
            error
          );

          this.errorMessage =
            'Failed to update application stage.';
        }
      });
  }
}