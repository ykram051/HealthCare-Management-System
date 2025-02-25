<template>
  <div v-if="showRescheduleModal" class="reschedule-modal">
    <div class="modal-content">
      <h2>Reschedule Appointment</h2>
      <p>Update the date and time for your appointment:</p>

      <label for="newDate">New Date:</label>
      <div class="input-wrapper">
        <input
          id="newDate"
          type="date"
          v-model="rescheduleData.newDate"
          required
          @change="fetchBookedSlots"
          placeholder="Select a date"
        />
        <i class="fa fa-calendar icon"></i>
      </div>

      <label for="newTime">New Time Slot:</label>
      <div class="input-wrapper">
        <select id="newTime" v-model="rescheduleData.newTime" required>
          <option
            v-for="slot in availableSlots"
            :key="slot"
            :value="slot"
            :disabled="bookedSlots.includes(slot)"
          >
            {{ slot }}
          </option>
        </select>
        <i class="fa fa-clock icon"></i>
      </div>

      <div class="modal-actions">
        <button @click="submitReschedule" :disabled="!isFormValid">
          Reschedule
        </button>
        <button @click="closeModal">Cancel</button>
      </div>
    </div>
  </div>
</template>

<script>
import api from "@/services/api";

export default {
  props: {
    appointmentId: {
      type: Number,
      required: true,
    },
    showRescheduleModal: {
      type: Boolean,
      required: true,
    },
  },
  data() {
    return {
      rescheduleData: {
        newDate: "",
        newTime: "",
      },
      availableSlots: [],
      bookedSlots: [],
    };
  },
  computed: {
    isFormValid() {
      return this.rescheduleData.newDate && this.rescheduleData.newTime;
    },
  },
  methods: {
    closeModal() {
      this.$emit("close-modal");
    },
    async fetchBookedSlots() {
  if (!this.rescheduleData.newDate) return;

  try {
    const url = `/appointments/doctor/${this.appointmentId}?date=${this.rescheduleData.newDate}`;
    const response = await api.get(url);

    // Remove duplicates from booked slots
    const uniqueSlots = [...new Set(response.map((appointment) => appointment.appointmentTime))];
    this.bookedSlots = uniqueSlots;

    // Generate available slots
    this.generateAvailableSlots();
  } catch (error) {
    console.error("Error fetching booked slots:", error.message);
    alert("Failed to fetch booked slots. Please try again.");
  }
}


,
generateAvailableSlots() {
  const startTime = new Date();
  startTime.setHours(9, 0, 0); // Start at 9:00 AM
  const endTime = new Date();
  endTime.setHours(17, 0, 0); // End at 5:00 PM

  const slots = [];
  while (startTime < endTime) {
    const slot = startTime.toLocaleTimeString([], {
      hour: "2-digit",
      minute: "2-digit",
      hour12: false, // Use 24-hour format
    });
    if (!this.bookedSlots.includes(slot)) {
      slots.push(slot);
    }
    startTime.setMinutes(startTime.getMinutes() + 30);
  }

  this.availableSlots = slots;
}
,
async submitReschedule() {
  if (!this.isFormValid) {
    alert("Please select both a new date and time slot.");
    return;
  }

  try {
    // Format the payload
    const requestData = {
      newDate: this.rescheduleData.newDate,
      newTime: this.rescheduleData.newTime, // Ensure this is in HH:mm format
    };

    // Send PUT request to reschedule the appointment
    const response = await api.put(`/appointments/${this.appointmentId}/reschedule`, requestData);

    // Handle plain text response
    if (typeof response === "string") {
      alert(response); // Show the plain text success message
    } else {
      alert("Appointment rescheduled successfully!"); // Fallback if response is not a string
    }

    this.$emit("reschedule-success"); // Notify the parent to update appointments
    this.closeModal(); // Close the modal
  } catch (error) {
    console.error("Error rescheduling appointment:", error);

    if (error.message) {
      alert(`Error: ${error.message}`);
    } else {
      alert("An unexpected error occurred. Please try again.");
    }
  }
}

,
  },
};
</script>

<style scoped>
.reschedule-modal {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
}

.modal-content {
  background: white;
  padding: 20px;
  border-radius: 10px;
  width: 400px;
  text-align: center;
}

.input-wrapper {
  position: relative;
}

input,
select {
  width: 100%;
  padding: 10px;
  padding-left: 35px;
  margin-bottom: 20px;
  font-size: 14px;
  border: 1px solid #ccc;
  border-radius: 5px;
}

input:focus,
select:focus {
  border-color: #004e89;
  outline: none;
}

.icon {
  position: absolute;
  left: 10px;
  top: 50%;
  transform: translateY(-50%);
  font-size: 16px;
  color: #888;
}

button {
  padding: 10px 20px;
  border: none;
  background: #004e89;
  color: white;
  border-radius: 5px;
  cursor: pointer;
  transition: background-color 0.3s;
}

button:hover {
  background: #003e6f;
}

button:disabled {
  background: #ccc;
  cursor: not-allowed;
}

.modal-actions {
  display: flex;
  justify-content: space-between;
  margin-top: 20px;
}
</style>
