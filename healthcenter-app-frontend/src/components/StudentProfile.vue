<template>
    <div class="profile-page">
      <div class="profile-header">
        <h1>{{ studentData.username }}'s Profile</h1>
      </div>
  
      <div class="profile-details">
        <h2>Profile Details</h2>
        <ul>
          <li><strong>Email:</strong> {{ studentData.email }}</li>
          <li><strong>Address:</strong> {{ studentData.address }}</li>
          <li><strong>Date of Birth:</strong> {{ studentData.dateOfBirth }}</li>
          <li>
            <strong>Allergies:</strong>
            {{ studentData.allergies?.length ? studentData.allergies.join(", ") : "None" }}
          </li>
        </ul>
      </div>
  
      <div class="treatments-section">
  <h2>Treatments</h2>
  <div v-if="treatments.length === 0" class="no-data">No treatments available.</div>
  <div v-else class="treatment-cards">
    <div
      v-for="treatment in treatments"
      :key="treatment.id"
      class="treatment-card"
      @click="redirectToEditTreatment(treatment.id)"
    >
      <h3>{{ treatment.name }}</h3>
      <p><strong>treatment Id:</strong> {{ treatment.id }}</p>
      <ul>
        <li v-for="medicine in treatment.medicines" :key="medicine.id">
          <p><strong>medication Name:</strong> {{ medicine.medicationName }}</p>
          <p><strong>How often:</strong> {{ medicine.howOften }} times/day</p>
        </li>
      </ul>
    </div>
  </div>
</div>

  
      <div class="appointments-section">
        <h2>Appointments</h2>
        <div v-if="appointments.length === 0" class="no-data">No appointments scheduled.</div>
        <div v-else class="appointment-cards">
          <div v-for="appointment in appointments" :key="appointment.id" class="appointment-card">
            <p><strong>Date:</strong> {{ appointment.appointmentDate }}</p>
            <p><strong>Time:</strong> {{ appointment.appointmentTime }}</p>
            <p><strong>Status:</strong> {{ appointment.status }}</p>
          </div>
        </div>
      </div>
    </div>
  </template>
  
  <script>
  import api from "@/services/api";
  
  export default {
    data() {
      return {
        studentData: {
          username: "",
          email: "",
          address: "",
          dateOfBirth: "",
          allergies: [], 
        },
        appointments: [],
        treatments: [],
      };
    },
methods: {
  async fetchStudentData() {
    try {
      const userId = JSON.parse(localStorage.getItem("user")).userId;
      this.studentData = await api.get(`/api/students/${userId}`);
      this.appointments = await api.get(`/api/students/${userId}/appointments`);
      this.treatments = await api.get(`/api/students/${userId}/treatments`);
    } catch (error) {
      console.error("Error fetching student data:", error.message);
      alert("Failed to load student profile.");
    }
  },
    redirectToEditTreatment(treatmentId) {
      this.$router.push({ name: "EditTreatment", params: { id: treatmentId } });
    }
  },
  
    mounted() {
      this.fetchStudentData();
    },
  };
  </script>
  
  <style scoped>
  .profile-page {
    font-family: "Roboto", Arial, sans-serif;
    max-width: 1200px;
    margin: auto;
    padding: 20px;
    background-color: #f9f9f9;
    border-radius: 8px;
    box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);
  }
  
  .profile-header {
    text-align: center;
    padding: 20px 0;
    background: linear-gradient(to right, #004e89, #007bb5);
    color: white;
    border-radius: 8px 8px 0 0;
  }
  
  .profile-header h1 {
    font-size: 28px;
  }
  
  .profile-details,
  .treatments-section,
  .appointments-section {
    margin: 20px 0;
  }
  
  .profile-details ul,
  .treatment-cards,
  .appointment-cards {
    margin-top: 10px;
    padding: 0;
    list-style: none;
  }
  
  .profile-details li {
    margin: 5px 0;
    font-size: 16px;
    color: #333;
  }
  
  .treatment-cards,
  .appointment-cards {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
    gap: 15px;
  }
  
  .treatment-card,
  .appointment-card {
    padding: 15px;
    background: white;
    border-radius: 8px;
    box-shadow: 0 2px 6px rgba(0, 0, 0, 0.1);
    transition: transform 0.3s ease, box-shadow 0.3s ease;
  }
  
  .treatment-card:hover,
  .appointment-card:hover {
    transform: translateY(-5px);
    box-shadow: 0 4px 10px rgba(0, 0, 0, 0.15);
  }
  
  .treatment-card h3 {
    font-size: 20px;
    color: #004e89;
  }
  
  .no-data {
    text-align: center;
    font-size: 16px;
    color: #777;
    margin: 10px 0;
  }
  </style>
  