import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ToastrService } from 'ngx-toastr';
import Swal from 'sweetalert2';
import { UserRequest, UserResponse, UserRole } from '../../../../core/models/user.models';
import { UsersService } from '../../services/admin.service';

@Component({
  selector: 'app-users-management',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './users-management.component.html',
  styleUrl: './users-management.component.css'
})
export class UsersManagementComponent implements OnInit {
  private usersService = inject(UsersService);
  private fb = inject(FormBuilder);
  private toastr = inject(ToastrService);

  loading = true;
  saving = false;
  editMode = false;
  editingUserId: number | null = null;

  users: UserResponse[] = [];
  filteredUsers: UserResponse[] = [];
  selectedUser: UserResponse | null = null;
  searchTerm = '';

  roles: UserRole[] = ['ADMIN', 'MANAGER', 'USER'];

  form = this.fb.group({
    fullName: ['', [Validators.required, Validators.minLength(2)]],
    email: ['', [Validators.required, Validators.email]],
    password: ['', [Validators.required, Validators.minLength(4)]],
    role: ['USER' as UserRole, Validators.required]
  });

  ngOnInit(): void {
    this.loadUsers();
  }

  loadUsers(): void {
    this.loading = true;

    this.usersService.getAll().subscribe({
      next: (data) => {
        this.users = data;
        this.applyFilter();
        this.loading = false;
      },
      error: () => {
        this.toastr.error('Impossible de charger les utilisateurs');
        this.loading = false;
      }
    });
  }

  onSubmit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.saving = true;

    const payload: UserRequest = {
      fullName: this.form.value.fullName ?? '',
      email: this.form.value.email ?? '',
      password: this.form.value.password ?? '',
      role: (this.form.value.role ?? 'USER') as UserRole
    };

    if (this.editMode && this.editingUserId) {
      this.usersService.update(this.editingUserId, payload).subscribe({
        next: (updatedUser) => {
          this.users = this.users.map(user =>
            user.id === updatedUser.id ? updatedUser : user
          );
          this.applyFilter();
          this.selectedUser = updatedUser;
          this.toastr.success('Utilisateur mis à jour avec succès');
          this.resetForm();
          this.saving = false;
        },
        error: (err) => {
          const message = err?.error?.message || 'Erreur lors de la mise à jour';
          this.toastr.error(message);
          this.saving = false;
        }
      });
    } else {
      this.usersService.create(payload).subscribe({
        next: (user) => {
          this.toastr.success('Utilisateur créé avec succès');
          this.users = [user, ...this.users];
          this.applyFilter();
          this.selectedUser = user;
          this.resetForm();
          this.saving = false;
        },
        error: (err) => {
          const message = err?.error?.message || 'Erreur lors de la création de l’utilisateur';
          this.toastr.error(message);
          this.saving = false;
        }
      });
    }
  }

  editUser(user: UserResponse): void {
    this.editMode = true;
    this.editingUserId = user.id;
    this.selectedUser = user;

    this.form.patchValue({
      fullName: user.fullName,
      email: user.email,
      password: '',
      role: user.role
    });

    this.form.get('password')?.clearValidators();
    this.form.get('password')?.updateValueAndValidity();
  }

  async deleteUser(user: UserResponse): Promise<void> {
    const result = await Swal.fire({
      title: 'Supprimer cet utilisateur ?',
      text: `${user.fullName} sera supprimé définitivement.`,
      icon: 'warning',
      showCancelButton: true,
      confirmButtonText: 'Oui, supprimer',
      cancelButtonText: 'Annuler',
      reverseButtons: true
    });

    if (!result.isConfirmed) return;

    this.usersService.delete(user.id).subscribe({
      next: () => {
        this.users = this.users.filter(u => u.id !== user.id);
        this.applyFilter();

        if (this.selectedUser?.id === user.id) {
          this.selectedUser = null;
        }

        if (this.editingUserId === user.id) {
          this.resetForm();
        }

        this.toastr.success('Utilisateur supprimé avec succès');
      },
      error: (err) => {
        const message = err?.error?.message || 'Erreur lors de la suppression';
        this.toastr.error(message);
      }
    });
  }

  onSearch(term: string): void {
    this.searchTerm = term;
    this.applyFilter();
  }

  applyFilter(): void {
    const term = this.searchTerm.trim().toLowerCase();

    if (!term) {
      this.filteredUsers = [...this.users];
      return;
    }

    this.filteredUsers = this.users.filter(user =>
      user.fullName.toLowerCase().includes(term) ||
      user.email.toLowerCase().includes(term) ||
      user.role.toLowerCase().includes(term)
    );
  }

  viewUser(user: UserResponse): void {
    this.selectedUser = user;
  }

  clearSelection(): void {
    this.selectedUser = null;
  }

  resetForm(): void {
    this.editMode = false;
    this.editingUserId = null;

    this.form.reset({
      fullName: '',
      email: '',
      password: '',
      role: 'USER'
    });

    this.form.get('password')?.setValidators([Validators.required, Validators.minLength(4)]);
    this.form.get('password')?.updateValueAndValidity();
  }

  cancelEdit(): void {
    this.resetForm();
  }

  trackByUserId(index: number, user: UserResponse): number {
    return user.id;
  }

  getRoleBadgeClass(role: string): string {
    switch (role) {
      case 'ADMIN':
        return 'badge-role badge-admin';
      case 'MANAGER':
        return 'badge-role badge-manager';
      case 'USER':
        return 'badge-role badge-user';
      default:
        return 'badge bg-secondary';
    }
  }
}