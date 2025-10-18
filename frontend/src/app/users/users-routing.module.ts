import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserFormComponent } from './pages/user-form/user-form.component';
import { UsersListComponent } from './pages/users-list/users-list.component';
import { UserViewComponent } from './pages/user-view/user-view.component';

const routes: Routes = [
  { path: 'users', component: UsersListComponent },
  { path: 'users/new', component: UserFormComponent },
  { path: 'users/:id/edit', component: UserFormComponent },
  { path: 'users/:id/view', component: UserViewComponent }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class UsersRoutingModule {}

