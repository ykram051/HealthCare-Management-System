<template>
    <div class="staff-profile">
      <h1>Welcome, Dr. {{ staffData.username }}</h1>
  
      <div class="profile-section">
        <h2>Profile Details</h2>
        <ul>
          <li><strong>Email:</strong> {{ staffData.email }}</li>
          <li><strong>Name:</strong> {{ staffData.username }}</li>
          <li><strong>Docto Id:</strong> {{ staffData.userId }}</li>


        </ul>
      </div>
  
      <div class="appointments-section">
        <h2>Appointments</h2>
        <div v-if="appointments.length === 0">No appointments available.</div>
        <ul v-else>
          <li v-for="appointment in appointments" :key="appointment.id">
            <strong>Student Id:</strong> {{ appointment.studentId }}<br />
            <strong>Date:</strong> {{ appointment.appointmentDate }}<br />
            <strong>Time:</strong> {{ appointment.appointmentTime }}<br />
            <strong>Status:</strong> {{ appointment.status }}
          </li>
        </ul>
      </div>
    </div>
  </template>
  
  <script>
  import api from "@/services/api";
  
  export default {
    data() {
      return {
        staffData: {},
        appointments: [],
      };
    },
    methods: {
      async fetchStaffData() {
        try {
          const staffId = JSON.parse(localStorage.getItem("user")).userId;
          this.staffData = await api.get(`/api/health-staff/${staffId}`);
          this.appointments = await api.get(`/api/health-staff/${staffId}/appointments`);
        } catch (error) {
          console.error("Error fetching staff data:", error.message);
          alert("Failed to load staff profile.");
        }
      },
    },
    mounted() {
      this.fetchStaffData();
    },
  };
  </script>
  
  <style scoped>
  .staff-profile {
    padding: 20px;
    font-family: Arial, sans-serif;
  }
  
  h1 {
    color: #004e89;
  }
  
  .profile-section,
  .appointments-section {
    margin-top: 20px;
  }
  
  ul {
    list-style: none;
    padding: 0;
  }
  
  li {
    margin-bottom: 10px;
  }
  </style>
  