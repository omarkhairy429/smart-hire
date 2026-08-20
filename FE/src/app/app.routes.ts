import { Routes } from '@angular/router';

export const routes: Routes = [
    {
        path: '',
        loadChildren: () => import('./features/postings/postings.routes').then((module) => module.POSTINGS_ROUTES)
    },
    { path: '**', redirectTo: '' }
];
