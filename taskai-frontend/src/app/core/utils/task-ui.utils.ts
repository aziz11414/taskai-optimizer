import { TaskPriority, TaskResponse, TaskStatus } from '../models/task.models';

export type RiskLevel = 'HIGH' | 'MEDIUM' | 'LOW';

export function getStatusClass(status: string): string {
  switch (status) {
    case 'TODO':
      return 'badge badge-todo';
    case 'IN_PROGRESS':
      return 'badge badge-progress';
    case 'DONE':
      return 'badge badge-done';
    default:
      return 'badge bg-secondary';
  }
}

export function getPriorityClass(priority: string): string {
  switch (priority) {
    case 'HIGH':
      return 'badge badge-high';
    case 'MEDIUM':
      return 'badge badge-medium';
    case 'LOW':
      return 'badge badge-low';
    default:
      return 'badge bg-secondary';
  }
}

export function getLevelClass(level?: string): string {
  switch (level) {
    case 'HIGH':
      return 'badge badge-high';
    case 'MEDIUM':
      return 'badge badge-medium';
    case 'LOW':
      return 'badge badge-low';
    default:
      return 'badge bg-secondary';
  }
}

export function getProbabilityClass(probability?: number): string {
  const value = probability ?? 0;
  if (value >= 70) return 'badge badge-high';
  if (value >= 40) return 'badge badge-medium';
  return 'badge badge-low';
}

export function getConfidenceClass(score?: number): string {
  const value = score ?? 0;
  if (value >= 80) return 'badge badge-done';
  if (value >= 60) return 'badge badge-progress';
  return 'badge badge-medium';
}

export function getScoreWidth(score?: number): string {
  return `${Math.max(0, Math.min(score ?? 0, 100))}%`;
}

export function formatScore(score?: number): string {
  return `${Math.round(score ?? 0)}`;
}

export function getDeadlineClass(task: TaskResponse): string {
  if (!task.dueDate) return 'deadline-normal';

  const now = Date.now();
  const due = new Date(task.dueDate).getTime();
  const diff = due - now;

  if (diff < 0) return 'deadline-urgent';
  if (diff < 2 * 86400000) return 'deadline-warning';

  return 'deadline-normal';
}

export function getRiskLevel(task: TaskResponse): RiskLevel {
  if (!task.dueDate) return task.priority === 'HIGH' ? 'MEDIUM' : 'LOW';

  const now = Date.now();
  const due = new Date(task.dueDate).getTime();
  const diff = due - now;

  if (diff < 0) return 'HIGH';
  if (task.priority === 'HIGH' && diff < 3 * 86400000) return 'HIGH';
  if (diff < 2 * 86400000) return 'MEDIUM';

  return task.priority === 'HIGH' ? 'MEDIUM' : 'LOW';
}

export function getTaskScore(task: TaskResponse): number {
  let score = 0;

  if (task.priority === 'HIGH') score += 45;
  if (task.priority === 'MEDIUM') score += 28;
  if (task.priority === 'LOW') score += 10;

  if (task.status === 'TODO') score += 18;
  if (task.status === 'IN_PROGRESS') score += 10;

  if (task.dueDate) {
    const diff = new Date(task.dueDate).getTime() - Date.now();

    if (diff < 0) score += 35;
    else if (diff < 86400000) score += 22;
    else if (diff < 3 * 86400000) score += 14;
  }

  return Math.min(score, 100);
}

export function isLikelyLate(task: TaskResponse): boolean {
  return getRiskLevel(task) === 'HIGH';
}