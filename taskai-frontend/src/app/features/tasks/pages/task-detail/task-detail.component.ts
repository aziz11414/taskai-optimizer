import { Component, OnInit, inject } from '@angular/core';
import { CommonModule, DatePipe } from '@angular/common';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { ToastrService } from 'ngx-toastr';

import { TasksService } from '../../services/tasks.service';
import { TaskResponse } from '../../../../core/models/task.models';
import { getPriorityClass, getStatusClass, isLikelyLate } from '../../../../core/utils/task-ui.utils';

@Component({
  selector: 'app-task-detail',
  standalone: true,
  imports: [CommonModule, DatePipe, RouterLink],
  templateUrl: './task-detail.component.html',
  styleUrl: './task-detail.component.css'
})
export class TaskDetailComponent implements OnInit {
  private readonly route = inject(ActivatedRoute);
  private readonly tasksService = inject(TasksService);
  private readonly toastr = inject(ToastrService);

  loading = true;
  task: TaskResponse | null = null;

  ngOnInit(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));

    this.tasksService.getById(id).subscribe({
      next: (data) => {
        this.task = data;
        this.loading = false;
      },
      error: () => {
        this.toastr.error('Impossible de charger la tâche');
        this.loading = false;
      }
    });
  }

  getStatusClass = getStatusClass;
  getPriorityClass = getPriorityClass;

  isLikelyLate(task: TaskResponse): boolean {
    return task.status !== 'DONE' && isLikelyLate(task);
  }
}