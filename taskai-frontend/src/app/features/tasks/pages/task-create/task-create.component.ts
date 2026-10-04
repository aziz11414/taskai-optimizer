import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { ToastrService } from 'ngx-toastr';

import { TasksService } from '../../services/tasks.service';
import { TaskRequest } from '../../../../core/models/task.models';

@Component({
  selector: 'app-task-create',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './task-create.component.html',
  styleUrl: './task-create.component.css'
})
export class TaskCreateComponent {
  private readonly fb = inject(FormBuilder);
  private readonly tasksService = inject(TasksService);
  private readonly router = inject(Router);
  private readonly toastr = inject(ToastrService);

  loading = false;

  form = this.fb.group({
    title: ['', [Validators.required, Validators.minLength(2)]],
    description: [''],
    status: ['TODO', Validators.required],
    priority: ['MEDIUM', Validators.required],
    dueDate: [''],
    userId: [null as number | null]
  });

  onSubmit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      this.toastr.warning('Merci de corriger les champs obligatoires');
      return;
    }

    this.loading = true;

    const value = this.form.getRawValue();

    const payload: TaskRequest = {
      title: value.title ?? '',
      description: value.description ?? '',
      status: value.status as 'TODO' | 'IN_PROGRESS' | 'DONE',
      priority: value.priority as 'LOW' | 'MEDIUM' | 'HIGH',
      dueDate: value.dueDate || null,
      userId: value.userId ?? null
    };

    this.tasksService.create(payload).subscribe({
      next: () => {
        this.toastr.success('Tâche créée avec succès');
        this.router.navigate(['/tasks']);
      },
      error: (err) => {
        this.toastr.error(err?.error?.message || 'Erreur lors de la création');
        this.loading = false;
      },
      complete: () => {
        this.loading = false;
      }
    });
  }

  hasError(controlName: 'title' | 'status' | 'priority'): boolean {
    const control = this.form.get(controlName);
    return !!control && control.touched && control.invalid;
  }
}