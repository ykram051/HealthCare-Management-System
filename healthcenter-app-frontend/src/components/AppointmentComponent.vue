<template>
  <div class="appointment-page">
    <h1>Book an Appointment</h1>

    <div v-if="healthStaffList.length > 0" class="doctors-list">
      <div
        v-for="staff in healthStaffList"
        :key="staff.userId"
        class="doctor-item"
        @click="selectHealthStaff(staff)"
      >
        <img src="https://via.placeholder.com/100" alt="Doctor" />
        <h3>{{ staff.username }}</h3>
        <p class="specialty">{{ staff.specialty || "General Practitioner" }}</p>
        <p class="email"><i class="fas fa-envelope"></i> {{ staff.email }}</p>
        <p class="phone"><i class="fas fa-phone"></i> {{ staff.phoneNumber || "N/A" }}</p>
      </div>
    </div>

    <div v-else>
      <p>Loading doctors...</p>
    </div>

    <div v-if="selectedHealthStaff" class="appointment-form">
      <h2>Selected Doctor: {{ selectedHealthStaff.username }}</h2>
      <label for="date">Select Date:</label>
      <input
        id="date"
        type="date"
        v-model="appointment.date"
        @change="fetchBookedSlots"
        required
      />

      <label for="time">Select Time Slot:</label>
      <select id="time" v-model="appointment.timeSlot" required>
        <option value="" disabled selected>Select a time slot</option>
        <option
          v-for="slot in timeSlots"
          :key="slot"
          :value="slot"
          :disabled="isSlotDisabled(slot)"
        >
          {{ slot }}
        </option>
      </select>

      <button @click="bookAppointment" :disabled="!isFormValid || isLoading">
        Book Appointment
      </button>
    </div>
  </div>
</template>

<script>
import api from "@/services/api";

export default {
  data() {
    return {
      healthStaffList: [],
      selectedHealthStaff: null,
      appointment: {
        doctorId: null,
        date: "",
        timeSlot: "",
      },
      timeSlots: [],
      bookedSlots: [],
      isLoading: false,
    };
  },
  computed: {
    isFormValid() {
      return this.appointment.doctorId && this.appointment.date && this.appointment.timeSlot;
    },
  },
  methods: {
    async fetchHealthStaff() {
      try {
        const response = await api.get("/api/health-staff");
        this.healthStaffList = response;
        console.log("Health staff loaded:", this.healthStaffList);
      } catch (error) {
        console.error("Error fetching health staff:", error.message);
        alert("Failed to load health staff. Please check your login or try again.");
      }
    },

    selectHealthStaff(staff) {
      this.selectedHealthStaff = staff;
      this.appointment.doctorId = staff.userId;
      this.generateTimeSlots();
      this.appointment.timeSlot = "";
      this.bookedSlots = [];
    },

    generateTimeSlots() {
      const slots = [];
      const startTime = new Date();
      startTime.setHours(9, 0, 0);
      const endTime = new Date();
      endTime.setHours(17, 0, 0);

      while (startTime < endTime) {
        const timeString = startTime.toLocaleTimeString('en-US', { 
          hour: 'numeric', 
          minute: '2-digit',
          hour12: true 
        });
        slots.push(timeString);
        startTime.setMinutes(startTime.getMinutes() + 30);
      }
      this.timeSlots = slots;
    },

    async fetchBookedSlots() {
      if (!this.appointment.date || !this.selectedHealthStaff) return;

      try {
        const url = `/appointments/doctor/${this.selectedHealthStaff.userId}?date=${this.appointment.date}`;
        const response = await api.get(url);
        this.bookedSlots = response.map((appointment) => appointment.appointmentTime) || [];
        console.log("Booked slots:", this.bookedSlots);
      } catch (error) {
        console.error("Error fetching booked slots:", error.message);
        alert("Failed to fetch booked slots. Please try again.");
      }
    },

    isSlotDisabled(slot) {
      if (!this.appointment.date) return true;

      const currentDate = new Date();
      const selectedDate = new Date(this.appointment.date);
      
      // Reset current date's time to midnight for date comparison
      const currentDateMidnight = new Date(currentDate);
      currentDateMidnight.setHours(0, 0, 0, 0);
      
      // Reset selected date's time to midnight for date comparison
      const selectedDateMidnight = new Date(selectedDate);
      selectedDateMidnight.setHours(0, 0, 0, 0);

      // If selected date is in the past, disable all slots
      if (selectedDateMidnight < currentDateMidnight) {
        return true;
      }

      // For current date, disable past time slots
      if (selectedDateMidnight.getTime() === currentDateMidnight.getTime()) {
        const time = new Date(`${this.appointment.date} ${slot}`);
        if (time < currentDate) {
          return true;
        }
      }

      // Check if slot is already booked
      return this.bookedSlots.includes(this.convertTo24HourFormat(slot));
    },

    convertTo24HourFormat(timeSlot) {
      // Extract hours and minutes from the time slot (e.g., "1:30 PM")
      const [time, period] = timeSlot.split(' ');
      const [hours, minutes] = time.split(':');
      
      let hour24 = parseInt(hours, 10);
      
      // Convert to 24-hour format
      if (period === 'PM' && hour24 !== 12) {
        hour24 += 12;
      } else if (period === 'AM' && hour24 === 12) {
        hour24 = 0;
      }
      
      // Return formatted time string (HH:mm:ss)
      return `${hour24.toString().padStart(2, '0')}:${minutes}:00`;
    },

    async bookAppointment() {
      if (!this.isFormValid) {
        alert("Please complete all fields before booking.");
        return;
      }

      this.isLoading = true;

      try {
        const studentId = JSON.parse(localStorage.getItem("user")).userId;
        const timeSlot24 = this.convertTo24HourFormat(this.appointment.timeSlot);
        
        console.log("Booking appointment with 24-hour format time:", timeSlot24);

        await api.post("/appointments", {
          doctorId: this.appointment.doctorId,
          studentId: studentId,
          appointmentDate: this.appointment.date,
          appointmentTime: timeSlot24,
        });

        alert("Appointment booked successfully!");
        this.resetForm();
      } catch (error) {
        console.error("Error booking appointment:", error.message);
        alert("Failed to book appointment. Please try again.");
      } finally {
        this.isLoading = false;
      }
    },

    resetForm() {
      this.selectedHealthStaff = null;
      this.appointment = { doctorId: null, date: "", timeSlot: "" };
      this.bookedSlots = [];
    },
  },

  mounted() {
    this.fetchHealthStaff();
  },
};
</script>
<style scoped>
/* General Layout */
.appointment-page {
  padding: 40px;
  max-width: 1200px;
  margin: 0 auto;
  font-family: 'Roboto', sans-serif;
  background-color: #f7f7f7;
  color: #333;
}

/* Doctor List */
.doctors-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 20px;
  margin: 30px 0;
}

.doctor-item {
  background-color: #ffffff;
  border: 1px solid #ddd;
  border-radius: 8px;
  padding: 20px;
  cursor: pointer;
  transition: transform 0.3s ease, box-shadow 0.3s ease;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
}

.doctor-item:hover {
  transform: translateY(-4px);
  box-shadow: 0 6px 12px rgba(0, 0, 0, 0.15);
}

.doctor-item img {
  width: 100px;
  height: 100px;
  border-radius: 50%;
  margin-bottom: 15px;
  object-fit: cover;
}

.doctor-item h3 {
  font-size: 1.2em;
  margin: 10px 0;
  color: #4CAF50;
}

.doctor-item p {
  margin: 5px 0;
  font-size: 14px;
  color: #555;
}

.doctor-item .email,
.doctor-item .phone {
  font-size: 13px;
  color: #888;
}

/* Appointment Form */
.appointment-form {
  max-width: 500px;
  margin: 20px auto;
  padding: 20px;
  background-color: #ffffff;
  border: 1px solid #ddd;
  border-radius: 8px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
}

.appointment-form h2 {
  color: #4CAF50;
  font-size: 1.4em;
  margin-bottom: 20px;
  text-align: center;
}

label {
  font-size: 16px;
  font-weight: 600;
  color: #333;
  margin-bottom: 8px;
  display: block;
}

/* Input Fields and Select */
.input-wrapper {
  position: relative;
  margin-bottom: 20px;
}

.input-wrapper input,
.input-wrapper select {
  width: 100%;
  padding: 12px 15px;
  padding-left: 35px;
  font-size: 14px;
  border: 1px solid #ddd;
  border-radius: 6px;
  margin: 8px 0;
  box-sizing: border-box;
}

.input-wrapper input:focus,
.input-wrapper select:focus {
  border-color: #4CAF50;
  outline: none;
}

.input-wrapper .icon {
  position: absolute;
  left: 15px;
  top: 50%;
  transform: translateY(-50%);
  color: #666;
  font-size: 18px;
}

/* Book Appointment Button */
.book-button {
  width: 100%;
  padding: 12px;
  background-color: #4CAF50;
  color: white;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-size: 16px;
  transition: background-color 0.3s ease;
}

.book-button:disabled {
  background-color: #cccccc;
  cursor: not-allowed;
}

.book-button:hover:not(:disabled) {
  background-color: #45a049;
}

/* Responsive Design */
@media screen and (max-width: 768px) {
  .appointment-page {
    padding: 20px;
  }

  .doctors-list {
    grid-template-columns: 1fr 1fr;
    gap: 15px;
  }

  .doctor-item {
    padding: 15px;
    text-align: center;
  }

  .appointment-form {
    padding: 15px;
  }

  .book-button {
    font-size: 14px;
    padding: 10px;
  }
}

@media screen and (max-width: 480px) {
  .doctors-list {
    grid-template-columns: 1fr;
    gap: 10px;
  }

  .doctor-item {
    padding: 10px;
    text-align: center;
  }

  .appointment-form {
    padding: 10px;
    max-width: 100%;
  }

  .book-button {
    font-size: 14px;
    padding: 12px;
  }
}
</style>
