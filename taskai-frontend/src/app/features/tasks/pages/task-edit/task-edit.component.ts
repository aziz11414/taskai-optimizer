import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { ToastrService } from 'ngx-toastr';

import { TasksService } from '../../services/tasks.service';
import { TaskRequest } from '../../../../core/models/task.models';

@Component({
  selector: 'app-task-edit',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './task-edit.component.html',
  styleUrl: './task-edit.component.css'
})
export class TaskEditComponent implements OnInit {
  private readonly fb = inject(FormBuilder);
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly tasksService = inject(TasksService);
  private readonly toastr = inject(ToastrService);

  taskId!: number;
  loading = true;
  saving = false;

  form = this.fb.group({
    title: ['', [Validators.required, Validators.minLength(2)]],
    description: [''],
    status: ['TODO', Validators.required],
    priority: ['MEDIUM', Validators.required],
    dueDate: [''],
    userId: [null as number | null]
  });

  ngOnInit(): void {
    this.taskId = Number(this.route.snapshot.paramMap.get('id'));

    this.tasksService.getById(this.taskId).subscribe({
      next: (task) => {
        this.form.patchValue({
          title: task.title,
          description: task.description,
          status: task.status,
          priority: task.priority,
          dueDate: task.dueDate ? this.formatDateTimeLocal(task.dueDate) : '',
          userId: task.userId
        });
        this.loading = false;
      },
      error: () => {
        this.toastr.error('Impossible de charger la tâche');
        this.loading = false;
      }
    });
  }

  onSubmit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      this.toastr.warning('Merci de corriger les champs obligatoires');
      return;
    }

    this.saving = true;

    const value = this.form.getRawValue();

    const payload: TaskRequest = {
      title: value.title ?? '',
      description: value.description ?? '',
      status: value.status as 'TODO' | 'IN_PROGRESS' | 'DONE',
      priority: value.priority as 'LOW' | 'MEDIUM' | 'HIGH',
      dueDate: value.dueDate || null,
      userId: value.userId ?? null
    };

    this.tasksService.update(this.taskId, payload).subscribe({
      next: () => {
        this.toastr.success('Tâche mise à jour');
        this.router.navigate(['/tasks']);
      },
      error: (err) => {
        this.toastr.error(err?.error?.message || 'Erreur lors de la mise à jour');
        this.saving = false;
      },
      complete: () => {
        this.saving = false;
      }
    });
  }

  hasError(controlName: 'title' | 'status' | 'priority'): boolean {
    const control = this.form.get(controlName);
    return !!control && control.touched && control.invalid;
  }

  private formatDateTimeLocal(dateString: string): string {
    const date = new Date(dateString);
    const offset = date.getTimezoneOffset();
    const localDate = new Date(date.getTime() - offset * 60000);
    return localDate.toISOString().slice(0, 16);
  }
}