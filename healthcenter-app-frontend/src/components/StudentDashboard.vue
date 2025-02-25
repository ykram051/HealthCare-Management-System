<template>
  <div class="student-dashboard">
    <div class="dashboard-header">
      <div class="header-content">
        <h1>Welcome, {{ studentName }}</h1>
        <p>Your personalized dashboard for managing appointments and treatments.</p>
        <div class="header-actions">
          <router-link to="/appointments" class="book-link">Book an Appointment</router-link>
          <router-link to="/treatments" class="add-treatments-link">
            <i class="fas fa-plus-circle"></i> Add Treatments
          </router-link>
       
        </div>
      </div>
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

    <div class="timeline-section">
      <h2>{{ selectedTab }} Appointments</h2>
      <div v-if="isLoadingAppointments" class="loading">Loading your appointments...</div>
      <div v-else-if="filteredAppointments.length === 0" class="no-appointments">
        <p>No appointments for the selected period. Book your appointment now!</p>
      </div>
      <div v-else class="appointments-timeline">
        <div
          v-for="appointment in filteredAppointments"
          :key="appointment.id"
          class="timeline-item"
        >
          <div class="timeline-date">{{ appointment.appointmentDate }}</div>
          <div class="timeline-content">
            <h3>Time: {{ appointment.appointmentTime }}</h3>
            <p><strong>Status:</strong> {{ appointment.status }}</p>
            <div class="timeline-actions">
  <button @click="cancelAppointment(appointment.id)" class="cancel-button">Cancel</button>
  <button @click="openRescheduleModal(appointment.id)" class="reschedule-button">Reschedule</button>
</div>

          </div>
        </div>
      </div>
    </div>

    <div class="medicine-tracking">
      <h2>{{ selectedTab }} Medicines</h2>
      <div v-if="isLoadingMedicines" class="loading">Loading your medicines...</div>
      <div v-else-if="filteredMedicines.length === 0" class="no-medicines">
        <p>No medicines for the selected period. You're all set!</p>
      </div>
      <div v-else>
        <div v-for="medicine in filteredMedicines" :key="medicine.id" class="medicine-item">
          <h3>{{ medicine.medicationName }}</h3>
          <p><strong>Doses Per Day:</strong> {{ medicine.howOften }}</p>
          <p><strong>Start Date:</strong> {{ medicine.startDate }}</p>
          <p><strong>End Date:</strong> {{ medicine.endDate }}</p>
          <div>
            <p>Doses Taken:</p>
            <div>
              <span
                v-for="dose in medicine.doses"
                :key="dose.time"
                :class="['dose-tracker', dose.taken ? 'dose-taken' : '']"
                @click="toggleDose(medicine.id, dose.time)"
              >
                {{ dose.time }}
              </span>
            </div>
          </div>
          <div class="progress">
            Progress: {{ getMedicineProgress(medicine) }}%
            <div class="progress-bar">
              <div
                class="progress-fill"
                :style="{ width: getMedicineProgress(medicine) + '%' }"
              ></div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <RescheduleModal
      v-if="showRescheduleModal"
      :appointmentId="selectedAppointmentId"
      :showRescheduleModal="showRescheduleModal"
      @close-modal="closeRescheduleModal"
      @reschedule-success="handleRescheduleSuccess"
    />

  </div>
</template>
<script>
import api from "@/services/api";
import RescheduleModal from "@/components/RescheduleModal.vue";

export default {
  name: "StudentDashboard",
  components: {
    RescheduleModal,
  },
  data() {
    return {
      studentName: "Student",
      appointments: [],
      medicines: [],
      isLoadingAppointments: true,
      isLoadingMedicines: true,
      showRescheduleModal: false,
      selectedAppointmentId: null,
      selectedTab: "Today", 
      tabs: ["Today", "Week", "Month", "Past"], 
    };
  },
  computed: {
    sortedAppointments() {
      const uniqueAppointments = [];
      const seen = new Set();

      this.appointments.forEach((appointment) => {
        const uniqueKey =
          appointment.appointmentDate + "-" + appointment.appointmentTime;
        if (!seen.has(uniqueKey)) {
          seen.add(uniqueKey);
          uniqueAppointments.push(appointment);
        }
      });

      return uniqueAppointments.sort(
        (a, b) =>
          new Date(a.appointmentDate + "T" + a.appointmentTime) -
          new Date(b.appointmentDate + "T" + b.appointmentTime)
      );
    },
    filteredAppointments() {
      const today = new Date();
      if (this.selectedTab === "Today") {
        return this.sortedAppointments.filter((appointment) => {
          const appointmentDate = new Date(appointment.appointmentDate);
          const [hours, minutes] = appointment.appointmentTime
            .split(":")
            .map(Number);
          appointmentDate.setHours(hours, minutes, 0);

          return (
            appointmentDate.toDateString() === today.toDateString() &&
            appointmentDate > today && 
            appointment.status === "UPCOMING"
          );
        });
      } else if (this.selectedTab === "Week") {
        const weekStart = new Date(today);
        weekStart.setDate(today.getDate() - today.getDay());
        const weekEnd = new Date(weekStart);
        weekEnd.setDate(weekEnd.getDate() + 6);

        return this.sortedAppointments.filter((appointment) => {
          const appointmentDate = new Date(appointment.appointmentDate);
          return (
            appointmentDate >= weekStart &&
            appointmentDate <= weekEnd &&
            appointmentDate > today && 
            appointment.status === "UPCOMING"
          );
        });
      } else if (this.selectedTab === "Month") {
        const monthStart = new Date(today.getFullYear(), today.getMonth(), 1);
        const monthEnd = new Date(today.getFullYear(), today.getMonth() + 1, 0);

        return this.sortedAppointments.filter((appointment) => {
          const appointmentDate = new Date(appointment.appointmentDate);
          return (
            appointmentDate >= monthStart &&
            appointmentDate <= monthEnd &&
            appointmentDate > today && 
            appointment.status === "UPCOMING"
          );
        });
      } else if (this.selectedTab === "Past") {
        return this.sortedAppointments.filter((appointment) => {
          const appointmentDate = new Date(appointment.appointmentDate);
          const [hours, minutes] = appointment.appointmentTime
            .split(":")
            .map(Number);
          appointmentDate.setHours(hours, minutes, 0);

          return appointmentDate < today && appointment.status === "PAST";
        });
      }
      return [];
    },
    filteredMedicines() {
      const today = new Date();
      if (this.selectedTab === "Today") {
        return this.medicines.filter(
          (medicine) =>
            today >= new Date(medicine.startDate) &&
            today <= new Date(medicine.endDate)
        );
      } else if (this.selectedTab === "Week") {
        const weekStart = new Date(today);
        weekStart.setDate(today.getDate() - today.getDay());
        const weekEnd = new Date(weekStart);
        weekEnd.setDate(weekEnd.getDate() + 6);

        return this.medicines.filter(
          (medicine) =>
            new Date(medicine.startDate) <= weekEnd &&
            new Date(medicine.endDate) >= weekStart
        );
      } else if (this.selectedTab === "Month") {
        const monthStart = new Date(today.getFullYear(), today.getMonth(), 1);
        const monthEnd = new Date(today.getFullYear(), today.getMonth() + 1, 0);

        return this.medicines.filter(
          (medicine) =>
            new Date(medicine.startDate) <= monthEnd &&
            new Date(medicine.endDate) >= monthStart
        );
      }
      return [];
    },
  },
  methods: {
    setTab(tab) {
      this.selectedTab = tab;
    },
    openRescheduleModal(appointmentId) {
      this.selectedAppointmentId = appointmentId;
      this.showRescheduleModal = true;
    },
    closeRescheduleModal() {
      this.showRescheduleModal = false;
      this.selectedAppointmentId = null;
    },
    async handleRescheduleSuccess() {
      await this.fetchAppointments();
      alert("Your appointment has been rescheduled successfully.");
    },
    async fetchAppointments() {
      try {
        const user = JSON.parse(localStorage.getItem("user"));
        const userId = user.userId;
        const response = await api.get(`/api/students/${userId}/appointments`);
        this.appointments = response;
      } catch (error) {
        console.error("Error fetching appointments:", error.message);
        alert("Failed to load appointments. Please try again later.");
      } finally {
        this.isLoadingAppointments = false;
      }
    },
    async cancelAppointment(appointmentId) {
      try {
        const confirmation = confirm(
          "Are you sure you want to cancel this appointment?"
        );
        if (!confirmation) return;

        await api.delete(`/appointments/${appointmentId}`);
        this.appointments = this.appointments.filter(
          (appointment) => appointment.id !== appointmentId
        );
        alert("Appointment cancelled successfully.");
      } catch (error) {
        console.error("Error cancelling appointment:", error.message);
        alert("Failed to cancel the appointment.");
      }
    },
    async fetchMedicines() {
      try {
        const user = JSON.parse(localStorage.getItem("user"));
        const userId = user.userId;
        const response = await api.get(`/api/students/${userId}/treatments`);

        this.medicines = response.flatMap((treatment) =>
          treatment.medicines.map((medicine) => ({
            ...medicine,
            doses: Array.from({ length: medicine.howOften }, (_, i) => ({
              time: `Dose ${i + 1}`,
              taken: false,
            })),
          }))
        );
      } catch (error) {
        console.error("Error fetching medicines:", error.message);
        alert("Failed to load medicines.");
      } finally {
        this.isLoadingMedicines = false;
      }
    },
    toggleDose(medicineId, doseTime) {
      const medicine = this.medicines.find((m) => m.id === medicineId);
      if (!medicine) return;

      const dose = medicine.doses.find((d) => d.time === doseTime);
      if (dose) dose.taken = !dose.taken;
    },
    getMedicineProgress(medicine) {
      const takenDoses = medicine.doses.filter((dose) => dose.taken).length;
      return Math.round((takenDoses / medicine.doses.length) * 100);
    },
  },
  mounted() {
    const user = JSON.parse(localStorage.getItem("user"));
    if (user) this.studentName = user.username;
    this.fetchAppointments();
    this.fetchMedicines();
  },
};
</script>


<style scoped>
.student-dashboard {
  font-family: "Arial", sans-serif;
  color: #333;
  background-color: #f9f9f9;
  min-height: 100vh;
  padding: 20px;
}

.dashboard-header {
  background: linear-gradient(to right, #004e89, #007bb5);
  color: white;
  text-align: center;
  padding: 50px 20px;
  border-radius: 10px;
}

.dashboard-header .header-actions {
  margin-top: 20px;
}

.dashboard-header .book-link,
.dashboard-header .add-treatments-link {
  background-color: white;
  color: #004e89;
  padding: 10px 20px;
  text-decoration: none;
  border-radius: 5px;
  margin: 10px;
  display: inline-block;
  font-weight: bold;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2);
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}

.dashboard-header .book-link:hover,
.dashboard-header .add-treatments-link:hover {
  transform: translateY(-3px);
  box-shadow: 0 6px 12px rgba(0, 0, 0, 0.3);
}

.dashboard-header .add-treatments-link i {
  margin-right: 8px;
}
.student-dashboard {
  font-family: "Arial", sans-serif;
  color: #333;
  background-color: #f9f9f9;
  min-height: 100vh;
  padding: 20px;
}

.dashboard-header {
  background: linear-gradient(to right, #004e89, #007bb5);
  color: white;
  text-align: center;
  padding: 50px 20px;
  border-radius: 10px;
}

.dashboard-header .book-link {
  background-color: white;
  color: #004e89;
  padding: 10px 20px;
  text-decoration: none;
  border-radius: 5px;
  display: inline-block;
  margin-top: 20px;
  font-weight: bold;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2);
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}

.dashboard-header .book-link:hover {
  transform: translateY(-3px);
  box-shadow: 0 6px 12px rgba(0, 0, 0, 0.3);
}

.timeline-section {
  margin-top: 30px;
  padding: 20px;
}

.appointments-timeline {
  border-left: 4px solid #004e89;
  margin: 20px 0;
  position: relative;
}

.timeline-item {
  margin-left: 20px;
  margin-bottom: 30px;
  position: relative;
}

.timeline-item::before {
  content: "";
  position: absolute;
  width: 12px;
  height: 12px;
  background-color: #004e89;
  border-radius: 50%;
  left: -7px;
  top: 10px;
}

.timeline-date {
  font-weight: bold;
  color: #004e89;
}

.timeline-content {
  background: white;
  border: 1px solid #ddd;
  padding: 15px;
  border-radius: 8px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}

.timeline-content:hover {
  transform: scale(1.02);
  box-shadow: 0 6px 12px rgba(0, 0, 0, 0.2);
}

.treatments-carousel {
  display: flex;
  gap: 20px;
  overflow-x: auto;
  padding: 20px 0;
}

.treatment-card {
  background-color: white;
  border: 1px solid #ddd;
  border-radius: 10px;
  width: 300px;
  padding: 20px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
  flex: 0 0 auto;
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}

.treatment-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 16px rgba(0, 0, 0, 0.2);
}

.treatment-card h3 {
  color: #004e89;
}

.medicine-item {
  display: flex;
  align-items: center;
  gap: 15px;
  margin-bottom: 10px;
  padding: 10px;
  background-color: #f4f8fb;
  border: 1px solid #e0e0e0;
  border-radius: 8px;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.1);
  transition: background-color 0.3s ease, box-shadow 0.3s ease;
}

.medicine-item:hover {
  background-color: #e6f7ff;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2);
}

.medicine-item input[type="checkbox"] {
  width: 20px;
  height: 20px;
  cursor: pointer;
}

.dose-tracker {
  display: inline-block;
  padding: 6px 12px;
  margin: 5px;
  background-color: #f0f0f0;
  border: 1px solid #ddd;
  border-radius: 20px;
  font-size: 14px;
  color: #555;
  cursor: pointer;
  transition: background-color 0.2s ease, color 0.2s ease, transform 0.2s ease;
}

.dose-tracker:hover {
  background-color: #004e89;
  color: white;
}

.dose-taken {
  background-color: #4caf50 !important;
  color: white !important;
  border-color: #3e8e41;
}

.progress {
  margin-top: 15px;
  font-weight: bold;
  font-size: 14px;
}

.progress-bar {
  width: 100%;
  height: 12px;
  background-color: #ddd;
  border-radius: 6px;
  overflow: hidden;
  margin-top: 5px;
}

.progress-fill {
  height: 100%;
  background: linear-gradient(to right, #4caf50, #81c784);
  transition: width 0.3s ease-in-out;
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

</style>


