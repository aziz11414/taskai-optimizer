import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { CommonModule, DatePipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import Swal from 'sweetalert2';

import { TasksService } from '../../services/tasks.service';
import { TaskPriority, TaskResponse, TaskStatus } from '../../../../core/models/task.models';
import { TokenService } from '../../../../core/services/token.service';
import {
  getDeadlineClass,
  getPriorityClass,
  getRiskLevel,
  getStatusClass,
  getTaskScore,
  isLikelyLate
} from '../../../../core/utils/task-ui.utils';

@Component({
  selector: 'app-task-list',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink, DatePipe],
  templateUrl: './task-list.component.html',
  styleUrl: './task-list.component.css'
})
export class TaskListComponent implements OnInit {
  private readonly tasksService = inject(TasksService);
  private readonly tokenService = inject(TokenService);

  loading = true;

  tasks = signal<TaskResponse[]>([]);
  searchTerm = signal('');
  selectedStatus = signal<'ALL' | TaskStatus>('ALL');
  selectedPriority = signal<'ALL' | TaskPriority>('ALL');
  sortBy = signal<'createdAt' | 'dueDate' | 'title'>('createdAt');

  currentPage = signal(1);
  readonly pageSize = 5;

  filteredTasks = computed(() => {
    let result = [...this.tasks()];

    const search = this.searchTerm().trim().toLowerCase();
    const status = this.selectedStatus();
    const priority = this.selectedPriority();
    const sort = this.sortBy();

    if (search) {
      result = result.filter(task =>
        task.title?.toLowerCase().includes(search) ||
        task.description?.toLowerCase().includes(search) ||
        task.userFullName?.toLowerCase().includes(search) ||
        task.userEmail?.toLowerCase().includes(search)
      );
    }

    if (status !== 'ALL') {
      result = result.filter(task => task.status === status);
    }

    if (priority !== 'ALL') {
      result = result.filter(task => task.priority === priority);
    }

    result.sort((a, b) => {
      if (sort === 'title') {
        return (a.title || '').localeCompare(b.title || '');
      }

      if (sort === 'dueDate') {
        const aDate = a.dueDate ? new Date(a.dueDate).getTime() : Number.MAX_SAFE_INTEGER;
        const bDate = b.dueDate ? new Date(b.dueDate).getTime() : Number.MAX_SAFE_INTEGER;
        return aDate - bDate;
      }

      const aCreated = new Date(a.createdAt).getTime();
      const bCreated = new Date(b.createdAt).getTime();
      return bCreated - aCreated;
    });

    return result;
  });

  totalPages = computed(() => {
    const total = Math.ceil(this.filteredTasks().length / this.pageSize);
    return total > 0 ? total : 1;
  });

  paginatedTasks = computed(() => {
    const page = this.currentPage();
    const start = (page - 1) * this.pageSize;
    return this.filteredTasks().slice(start, start + this.pageSize);
  });

  ngOnInit(): void {
    this.loadTasks();
  }

  loadTasks(): void {
    this.loading = true;

    this.tasksService.getAll().subscribe({
      next: (data) => {
        this.tasks.set(data);
        this.loading = false;
      },
      error: () => {
        this.loading = false;
      }
    });
  }

  onSearchChange(value: string): void {
    this.searchTerm.set(value);
    this.currentPage.set(1);
  }

  onStatusChange(value: 'ALL' | TaskStatus): void {
    this.selectedStatus.set(value);
    this.currentPage.set(1);
  }

  onPriorityChange(value: 'ALL' | TaskPriority): void {
    this.selectedPriority.set(value);
    this.currentPage.set(1);
  }

  onSortChange(value: 'createdAt' | 'dueDate' | 'title'): void {
    this.sortBy.set(value);
    this.currentPage.set(1);
  }

  previousPage(): void {
    if (this.currentPage() > 1) {
      this.currentPage.update(v => v - 1);
    }
  }

  nextPage(): void {
    if (this.currentPage() < this.totalPages()) {
      this.currentPage.update(v => v + 1);
    }
  }

  async deleteTask(task: TaskResponse): Promise<void> {
    const result = await Swal.fire({
      title: 'Supprimer cette tâche ?',
      text: `${task.title} sera supprimée définitivement.`,
      icon: 'warning',
      showCancelButton: true,
      confirmButtonText: 'Oui, supprimer',
      cancelButtonText: 'Annuler',
      reverseButtons: true
    });

    if (!result.isConfirmed) return;

    this.tasksService.delete(task.id).subscribe({
      next: async () => {
        this.tasks.set(this.tasks().filter(t => t.id !== task.id));

        if (this.currentPage() > this.totalPages()) {
          this.currentPage.set(this.totalPages());
        }

        await Swal.fire({
          icon: 'success',
          title: 'Supprimée',
          text: 'La tâche a été supprimée avec succès.',
          timer: 1800,
          showConfirmButton: false
        });
      },
      error: async (err) => {
        await Swal.fire({
          icon: 'error',
          title: 'Erreur',
          text: err?.error?.message || 'Erreur lors de la suppression.'
        });
      }
    });
  }

  canDelete(): boolean {
    return this.tokenService.isAdmin();
  }

  getStatusClass = getStatusClass;
  getPriorityClass = getPriorityClass;
  getDeadlineClass = getDeadlineClass;
  getRiskLevel = getRiskLevel;
  getScore = getTaskScore;
  isLikelyLate = isLikelyLate;
}