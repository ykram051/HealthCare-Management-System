<template>
  <div>
    <h1>Treatment Management</h1>
    <div class="treatment-management">
      <div v-if="isLoading" class="loading">Loading treatments...</div>
      <div v-else-if="filteredTreatments.length === 0" class="no-treatments">
        <p>No active treatments for today. Create one below!</p>
      </div>
      <div v-else class="treatment-list">
        <div
          v-for="treatment in filteredTreatments"
          :key="treatment.id"
          class="treatment-item"
          @click="redirectToEditPage(treatment.id)"
        >
          <div class="treatment-header">
            <h3>{{ treatment.name }}</h3>
            <time class="treatment-dates">
              {{ treatment.startDate || "N/A" }} - {{ treatment.endDate || "N/A" }}
            </time>
          </div>
          <div class="treatment-body">
            <p class="medicine-list-title">Medicines for Today:</p>
            <ul class="medicine-list">
              <li v-for="medicine in treatment.activeMedicines" :key="medicine.id">
                <strong>{{ medicine.medicationName }}</strong>
                <span>({{ medicine.howOften }} times/day)</span>
              </li>
            </ul>
            <button
              @click.stop="deleteTreatment(treatment.id)"
              class="delete-button"
            >
              Delete Treatment
            </button>
          </div>
        </div>
      </div>

      <div class="treatment-form">
        <h2>Create a New Treatment</h2>
        <label for="name">Treatment Name:</label>
        <input
          id="name"
          v-model="newTreatment.name"
          type="text"
          :class="{ 'invalid-input': !newTreatment.name }"
          placeholder="Enter treatment name"
          required
        />

        <label for="medicine">Add Medicines:</label>
        <transition-group name="fade" tag="div">
          <div
            v-for="(medicine, index) in newTreatment.medicines"
            :key="index"
            class="medicine-form"
          >
            <div class="form-row">
              <input
                type="text"
                v-model="medicine.medicationName"
                placeholder="Medicine Name"
                required
              />
              <input
                type="number"
                v-model="medicine.howOften"
                placeholder="Frequency (times/day)"
                required
              />
            </div>
            <div class="form-row">
              <input type="date" v-model="medicine.startDate" required />
              <input type="date" v-model="medicine.endDate" required />
            </div>
            <button @click="removeMedicine(index)" class="remove-medicine">
              Remove
            </button>
          </div>
        </transition-group>

        <button
          @click="createTreatment"
          class="create-button"
          :disabled="!isFormValid"
        >
          Create Treatment
        </button>
      </div>

      <PrescriptionUpload />
    </div>
  </div>
</template>

<script>
import api from "@/services/api";
import PrescriptionUpload from "@/components/PrescriptionUpload";

export default {
  name: "TreatmentManagement",
  components: {
    PrescriptionUpload,
  },
  data() {
    return {
      treatments: [],
      isLoading: true,
      newTreatment: {
        name: "",
        medicines: [],
      },
    };
  },
  computed: {
    isFormValid() {
      return (
        this.newTreatment.name &&
        this.newTreatment.medicines.every(
          (medicine) =>
            medicine.medicationName &&
            medicine.howOften > 0 &&
            medicine.startDate &&
            medicine.endDate
        )
      );
    },
    filteredTreatments() {
      const today = new Date();
      return this.treatments
        .map((treatment) => ({
          ...treatment,
          activeMedicines: treatment.medicines.filter((medicine) => {
            const startDate = new Date(medicine.startDate);
            const endDate = new Date(medicine.endDate);
            return today >= startDate && today <= endDate;
          }),
        }))
        .filter((treatment) => treatment.activeMedicines.length > 0);
    },
  },
  methods: {
    async fetchTreatments() {
      try {
        const userId = JSON.parse(localStorage.getItem("user")).userId;
        const response = await api.get(`/api/students/${userId}/treatments`);

        this.treatments = response.map((treatment) => {
          const startDates = treatment.medicines.map(
            (med) => new Date(med.startDate)
          );
          const endDates = treatment.medicines.map(
            (med) => new Date(med.endDate)
          );

          return {
            ...treatment,
            startDate: startDates.length
              ? new Date(Math.min(...startDates)).toISOString().split("T")[0]
              : "N/A",
            endDate: endDates.length
              ? new Date(Math.max(...endDates)).toISOString().split("T")[0]
              : "N/A",
          };
        });
      } catch (error) {
        console.error("Error fetching treatments:", error.message);
        alert("Failed to load treatments.");
      } finally {
        this.isLoading = false;
      }
    },
    async createTreatment() {
      try {
        const userId = JSON.parse(localStorage.getItem("user")).userId;

        const treatmentPayload = {
          ...this.newTreatment,
          studentId: userId,
        };

        const createdTreatment = await api.post(`/api/treatments`, treatmentPayload);

        alert("Treatment created successfully!");

        this.treatments.push(createdTreatment);

        this.resetForm();
      } catch (error) {
        console.error("Error creating treatment:", error.message);
        alert("Failed to create treatment.");
      }
    },
    async deleteTreatment(treatmentId) {
      try {
        const confirmation = confirm(
          "Are you sure you want to delete this treatment?"
        );
        if (!confirmation) return;

        await api.delete(`/api/treatments/${treatmentId}`);
        alert("Treatment deleted successfully!");
        this.fetchTreatments();
      } catch (error) {
        console.error("Error deleting treatment:", error.message);
        alert("Failed to delete treatment.");
      }
    },
    addMedicine() {
      this.newTreatment.medicines.push({
        medicationName: "",
        howOften: 1,
        startDate: "",
        endDate: "",
      });
    },
    removeMedicine(index) {
      this.newTreatment.medicines.splice(index, 1);
    },
    resetForm() {
      this.newTreatment = { name: "", medicines: [] };
    },
    redirectToEditPage(treatmentId) {
      this.$router.push({ name: "EditTreatment", params: { id: treatmentId } });
    },
  },
  mounted() {
    this.fetchTreatments();
  },
};
</script>

<style scoped>
.treatment-management {
  font-family: 'Roboto', Arial, sans-serif;
  padding: 20px;
  max-width: 1200px;
  margin: auto;
}

h1,
h2 {
  color: #2c3e50;
  text-align: center;
}

.treatment-list {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 20px;
}

.treatment-item {
  background-color: #ffffff;
  padding: 20px;
  border-radius: 8px;
  border: 1px solid #e0e0e0;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}

.treatment-item:hover {
  transform: scale(1.02);
  box-shadow: 0 6px 12px rgba(0, 0, 0, 0.15);
}

.treatment-item h3 {
  color: #004e89;
  margin-bottom: 10px;
}

.treatment-item ul {
  padding-left: 20px;
}

.delete-button {
  background-color: #e74c3c;
  color: white;
  border: none;
  padding: 10px;
  border-radius: 5px;
  cursor: pointer;
  font-size: 14px;
  margin-top: 10px;
}

.delete-button:hover {
  background-color: #c0392b;
}
.treatment-management {
  font-family: "Roboto", sans-serif;
  padding: 20px;
  max-width: 1200px;
  margin: auto;
  background: #f4f8fb;
  border-radius: 15px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

h1,
h2 {
  color: #2c3e50;
  text-align: center;
}

.treatment-list {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 20px;
}

.treatment-item {
  background: #ffffff;
  padding: 20px;
  border-radius: 10px;
  border: 1px solid #e0e0e0;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.05);
  transition: transform 0.3s, box-shadow 0.3s;
}

.treatment-item:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 16px rgba(0, 0, 0, 0.1);
}

.treatment-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.treatment-header h3 {
  color: #004e89;
  margin-bottom: 10px;
}

.medicine-list-title {
  font-weight: bold;
  color: #2c3e50;
}

.medicine-list {
  padding-left: 20px;
  list-style: none;
}

.medicine-list li {
  font-size: 14px;
  color: #555;
  margin-bottom: 5px;
}

.delete-button {
  background-color: #e74c3c;
  color: white;
  border: none;
  padding: 10px 15px;
  border-radius: 8px;
  cursor: pointer;
  font-size: 14px;
  transition: background-color 0.3s, transform 0.2s;
}

.delete-button:hover {
  background-color: #c0392b;
  transform: translateY(-2px);
}

.treatment-form {
  margin-top: 40px;
  padding: 30px;
  background: #ffffff;
  border-radius: 15px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.treatment-form label {
  font-weight: bold;
  color: #2c3e50;
  margin-bottom: 10px;
  display: block;
  font-size: 16px;
}

.treatment-form input {
  width: 100%;
  padding: 12px 15px;
  border-radius: 8px;
  border: 1px solid #ccc;
  margin-top: 8px;
  transition: border-color 0.3s, box-shadow 0.3s;
  font-size: 14px;
  color: #333;
}

.treatment-form input:focus {
  border-color: #004e89;
  box-shadow: 0 0 8px rgba(0, 78, 137, 0.3);
  outline: none;
}

.medicine-form {
  background-color: #f4f8fb;
  padding: 20px;
  border: 1px solid #e0e0e0;
  border-radius: 10px;
  margin-top: 20px;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.1);
  transition: transform 0.2s, box-shadow 0.2s;
}

.medicine-form:hover {
  transform: scale(1.02);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.medicine-form .form-row {
  display: flex;
  gap: 20px;
  margin-top: 10px;
}

.medicine-form input {
  flex: 1;
}

.add-medicine,
.remove-medicine {
  background-color: #004e89;
  color: white;
  border: none;
  padding: 12px 20px;
  border-radius: 8px;
  font-size: 14px;
  cursor: pointer;
  transition: background-color 0.3s, transform 0.2s;
}

.add-medicine:hover,
.remove-medicine:hover {
  background-color: #007bb5;
  transform: translateY(-2px);
}

.add-medicine:active,
.remove-medicine:active {
  transform: scale(0.98);
}

.create-button {
  margin-top: 20px;
  background-color: #27ae60;
  color: white;
  border: none;
  padding: 15px 25px;
  font-size: 16px;
  border-radius: 10px;
  cursor: pointer;
  transition: background-color 0.3s, transform 0.2s;
}

.create-button:hover {
  background-color: #2ecc71;
  transform: translateY(-3px);
}

.create-button:disabled {
  background-color: #bdc3c7;
  cursor: not-allowed;
}

@media (max-width: 768px) {
  .medicine-form .form-row {
    flex-direction: column;
  }

  .add-medicine,
  .remove-medicine,
  .create-button {
    width: 100%;
    text-align: center;
  }
}
</style>