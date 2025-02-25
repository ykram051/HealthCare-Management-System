import { createRouter, createWebHistory } from 'vue-router';
import LandingPage from '@/components/LandingPage.vue';
import LoginComponent from '@/components/LoginComponent.vue';
import RegisterComponent from '@/components/RegisterComponent.vue';
import AppointmentComponent from "@/components/AppointmentComponent.vue";
import TreatmentManagement from "@/components/TreatmentManagement.vue";
import StudentProfile from "@/components/StudentProfile.vue";
import StaffProfile from "@/components/StaffProfile.vue";
import StudentProfile from "@/components/StudentProfile.vue";
import StaffProfile from "@/components/StaffProfile.vue";

const routes = [
  { path: '/', name: 'LandingPage', component: LandingPage },
  { path: '/login', name: 'Login', component: LoginComponent },
  { path: '/register', name: 'Register', component: RegisterComponent },
  {
    path: "/student-dashboard",
    name: "StudentDashboard",
    component: () => import("@/components/StudentDashboard.vue"),
  },
    { path: '/staff-dashboard', name: 'StaffDashboard', component: () => import('@/components/StaffDashboard.vue') },
  {path: "/appointments",name: "Appointments",component: AppointmentComponent,},
  { path: "/treatments", name: "Treatments", component: () => import("@/components/TreatmentManagement.vue") },
  {
    path: "/treatments",
    name: "TreatmentManagement",
    component: TreatmentManagement,
  },
  {
    path: "/treatments/:id/edit",
    name: "EditTreatment",
    component: () => import("@/components/EditTreatment.vue"),
    props: true,
  },
  
  { path: "/student-profile", name: "StudentProfile", component: StudentProfile },
  { path: "/staff-profile", name: "StaffProfile", component: StaffProfile },


];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

export default router;
