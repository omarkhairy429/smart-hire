// import { CommonModule } from '@angular/common';
// import { Component, OnInit, inject, signal } from '@angular/core';
// import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';

// import { Posting, LocationType, PostingRequest } from '../models/posting.model';
// import { PostingsService } from '../services/postings.service';

// @Component({
//     selector: 'app-postings-page',
//     standalone: true,
//     imports: [CommonModule, ReactiveFormsModule],
//     templateUrl: './postings-page.component.html',
//     styleUrl: './postings-page.component.css'
// })
// export class PostingsPageComponent implements OnInit {
//     private readonly formBuilder = inject(FormBuilder);
//     private readonly postingsService = inject(PostingsService);

//     readonly postings = signal<Posting[]>([]);
//     readonly isLoading = signal(true);
//     readonly isSubmitting = signal(false);
//     readonly errorMessage = signal('');
//     readonly successMessage = signal('');
//     readonly locationTypes: LocationType[] = ['REMOTE', 'HYBRID', 'ON_SITE'];

//     readonly postingForm = this.formBuilder.nonNullable.group({
//         hrManagerId: ['', [Validators.required, Validators.pattern(/^[0-9a-f]{8}-[0-9a-f]{4}-[1-5][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}$/i)]],
//         title: ['', [Validators.required, Validators.maxLength(120)]],
//         description: ['', [Validators.required, Validators.maxLength(3000)]],
//         skillsRequired: ['', Validators.required],
//         locationType: ['REMOTE' as LocationType, Validators.required],
//         location: ['', Validators.maxLength(160)],
//         deadline: ['', Validators.required]
//     });

//     ngOnInit(): void {
//         this.loadPostings();
//     }

//     loadPostings(): void {
//         this.isLoading.set(true);
//         this.errorMessage.set('');

//         this.postingsService.getPostings().subscribe({
//             next: (postings) => {
//                 this.postings.set(postings);
//                 this.isLoading.set(false);
//             },
//             error: () => {
//                 this.errorMessage.set('Unable to load job postings. Check that the backend is running.');
//                 this.isLoading.set(false);
//             }
//         });
//     }

//     createPosting(): void {
//         this.successMessage.set('');
//         this.errorMessage.set('');

//         if (this.postingForm.invalid) {
//             this.postingForm.markAllAsTouched();
//             return;
//         }

//         const formValue = this.postingForm.getRawValue();
//         const request: PostingRequest = {
//             ...formValue,
//             skillsRequired: formValue.skillsRequired.split(',').map((skill) => skill.trim()).filter(Boolean)
//         };

//         this.isSubmitting.set(true);
//         this.postingsService.createPosting(request).subscribe({
//             next: (posting) => {
//                 this.postings.update((postings) => [posting, ...postings]);
//                 this.successMessage.set('Job posting created successfully.');
//                 this.postingForm.reset({ locationType: 'REMOTE' });
//                 this.isSubmitting.set(false);
//             },
//             error: (error) => {
//                 this.errorMessage.set(error.error?.message ?? 'Unable to create the job posting.');
//                 this.isSubmitting.set(false);
//             }
//         });
//     }

//     fieldHasError(fieldName: string): boolean {
//         const field = this.postingForm.get(fieldName);
//         return !!field && field.invalid && field.touched;
//     }
// }