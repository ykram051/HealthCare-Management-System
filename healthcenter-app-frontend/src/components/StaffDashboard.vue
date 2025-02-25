<template>
  <div class="staff-dashboard">
    <div class="dashboard-header">
      <h1>Welcome, Dr. {{ staffName }}</h1>
      <p>Your personalized dashboard to manage appointments.</p>
    </div>

    <div class="date-filter-section">
      <button
        v-for="tab in tabs"
        :key="tab"
        :class="{ active: selectedTab === tab }"
        @click="setTab(tab)"
      >
        {{ tab }}
      </button>
    </div>

    <div class="appointments-section">
      <h2>{{ selectedTab }} Appointments</h2>

      <div v-if="isLoading" class="loading">Loading appointments...</div>
      <div v-else-if="filteredAppointments.length === 0" class="no-appointments">
        <p>No appointments scheduled for this period.</p>
      </div>

      <div v-else class="appointments-list">
        <div
          v-for="appointment in filteredAppointments"
          :key="appointment.id"
          class="appointment-card"
        >
          <div class="appointment-info">
            <h3>Appointment</h3>
            <p><strong>Appointment Id:</strong> {{ appointment.id }}</p>
            <p><strong>Date:</strong> {{ appointment.appointmentDate }}</p>
            <p><strong>Time:</strong> {{ appointment.appointmentTime }}</p>
            <p><strong>Status:</strong> {{ appointment.status }}</p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import api from "@/services/api";

export default {
  name: "StaffDashboard",
  data() {
    return {
      staffName: "",
      appointments: [],
      isLoading: true,
      selectedTab: "Today", 
      tabs: ["Today", "Week", "Month"],
    };
  },
  computed: {
    sortedAppointments() {
      // Sort appointments by date and time
      return [...this.appointments].sort((a, b) => {
        return (
          new Date(a.appointmentDate + "T" + a.appointmentTime) -
          new Date(b.appointmentDate + "T" + b.appointmentTime)
        );
      });
    },
    filteredAppointments() {
      const today = new Date();

      if (this.selectedTab === "Today") {
        return this.sortedAppointments.filter(
          (appointment) =>
            new Date(appointment.appointmentDate).toDateString() ===
            today.toDateString()
        );
      } else if (this.selectedTab === "Week") {
        const weekStart = new Date(today);
        weekStart.setDate(today.getDate() - today.getDay());
        const weekEnd = new Date(weekStart);
        weekEnd.setDate(weekEnd.getDate() + 6);

        return this.sortedAppointments.filter(
          (appointment) =>
            new Date(appointment.appointmentDate) >= weekStart &&
            new Date(appointment.appointmentDate) <= weekEnd
        );
      } else if (this.selectedTab === "Month") {
        const monthStart = new Date(today.getFullYear(), today.getMonth(), 1);
        const monthEnd = new Date(today.getFullYear(), today.getMonth() + 1, 0);

        return this.sortedAppointments.filter(
          (appointment) =>
            new Date(appointment.appointmentDate) >= monthStart &&
            new Date(appointment.appointmentDate) <= monthEnd
        );
      }
      return [];
    },
  },
  methods: {
    setTab(tab) {
      this.selectedTab = tab;
    },
    async fetchAppointments() {
      try {
        const user = JSON.parse(localStorage.getItem("user"));
        if (!user || !user.userId) {
          throw new Error("User ID not found. Please log in again.");
        }

        const userId = user.userId;

        const response = await api.get(`/api/health-staff/${userId}/appointments`);
        this.appointments = response.map((appointment) => ({
          id: appointment.id,
          studentName: appointment.studentName || "Unknown",
          appointmentDate: appointment.appointmentDate,
          appointmentTime: appointment.appointmentTime,
          status: appointment.status,
        }));
      } catch (error) {
        console.error("Error fetching appointments:", error.message);
        alert("Failed to load appointments. Please try again later.");
      } finally {
        this.isLoading = false;
      }
    },
  },
  mounted() {
    const user = JSON.parse(localStorage.getItem("user"));
    if (user && user.username) {
      this.staffName = user.username;
    } else {
      this.staffName = "Staff"; 
    }

    this.fetchAppointments();
  },
};
</script>

<style scoped>
.staff-dashboard {
  font-family: "Arial", sans-serif;
  color: #333;
  padding: 20px;
}

.dashboard-header {
  background: linear-gradient(to right, #004e89, #007bb5);
  color: white;
  text-align: center;
  padding: 50px 20px;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);
  border-radius: 10px;
}

.dashboard-header h1 {
  font-size: 36px;
  margin: 0;
}

.dashboard-header p {
  font-size: 18px;
  margin: 10px 0 0;
}

.date-filter-section {
  display: flex;
  justify-content: center;
  gap: 10px;
  margin: 20px 0;
}

.date-filter-section button {
  padding: 10px 20px;
  border: none;
  background-color: #e0e0e0;
  color: #555;
  border-radius: 20px;
  font-weight: bold;
  cursor: pointer;
  transition: background-color 0.3s, color 0.3s;
}

.date-filter-section button.active {
  background-color: #004e89;
  color: white;
}

.date-filter-section button:hover {
  background-color: #007bb5;
  color: white;
}

.appointments-section {
  margin-top: 40px;
}

h2 {
  font-size: 28px;
  color: #004e89;
  margin-bottom: 20px;
  text-align: center;
}

.loading {
  font-size: 18px;
  text-align: center;
  color: #777;
}

.no-appointments {
  font-size: 18px;
  text-align: center;
  color: #555;
  margin-top: 20px;
}

.appointments-list {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 20px;
}

.appointment-card {
  background-color: #fff;
  border-radius: 10px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
  padding: 20px;
  transition: transform 0.3s ease, box-shadow 0.3s ease;
}

.appointment-card:hover {
  transform: translateY(-10px);
  box-shadow: 0 6px 12px rgba(0, 0, 0, 0.2);
}

.appointment-info h3 {
  font-size: 20px;
  color: #004e89;
  margin-bottom: 10px;
}

.appointment-info p {
  font-size: 16px;
  margin: 5px 0;
  color: #555;
}
</style>
