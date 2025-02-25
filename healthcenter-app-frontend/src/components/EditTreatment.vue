<template>
  <div class="edit-treatment">
    <h1>Edit Treatment</h1>
    <div v-if="isLoading">Loading treatment data...</div>
    <div v-else-if="error" class="error">{{ error }}</div>
    <div v-else>
      <label for="name">Treatment Name:</label>
      <input
        type="text"
        id="name"
        v-model="treatment.name"
        placeholder="Enter treatment name"
      />

      <label for="description">Description:</label>
      <input
        type="text"
        id="description"
        v-model="treatment.description"
        placeholder="Enter description"
      />

      <label for="duration">Duration (days):</label>
      <input
        type="number"
        id="duration"
        v-model="treatment.duration"
        placeholder="Enter duration"
      />

      <h2>Medicines</h2>
      <ul>
        <li v-for="(medicine, index) in treatment.medicines" :key="medicine.id || index">
          <input
            type="text"
            v-model="medicine.medicationName"
            placeholder="Medicine Name"
          />
          <input
            type="number"
            v-model="medicine.howOften"
            placeholder="Frequency (times/day)"
          />
          <input
            type="date"
            v-model="medicine.startDate"
          />
          <input
            type="date"
            v-model="medicine.endDate"
          />
        </li>
      </ul>
      <button @click="saveTreatment" class="save-button">Save Changes</button>
    </div>
  </div>
</template>

<script>
import api from "@/services/api";

export default {
  name: "EditTreatment",
  data() {
    return {
      treatment: {
        name: "",
        description: "",
        duration: 0,
        medicines: [],
      },
      isLoading: true,
      error: null,
    };
  },
  methods: {
    async fetchTreatment() {
      try {
        const id = this.$route.params.id; 
        const response = await api.get(`/api/treatments/${id}`);
        this.treatment = response.data || {
          name: "",
          description: "",
          duration: 0,
          medicines: [],
        };
        if (!this.treatment.medicines) {
          this.treatment.medicines = [];
        }
      } catch (error) {
        console.error("Error fetching treatment:", error.message);
        this.error = "Failed to load treatment data.";
      } finally {
        this.isLoading = false;
      }
    },
    async saveTreatment() {
      try {
        const id = this.$route.params.id; 
        await api.put(`/api/treatments/${id}`, this.treatment); 
        alert("Treatment updated successfully!");
      } catch (error) {
        console.error("Error saving treatment:", error.message);
        this.error = "Failed to save treatment.";
      }
    },
  },
  mounted() {
    this.fetchTreatment(); 
  },
};
</script>

<style scoped>
.edit-treatment {
  font-family: "Roboto", Arial, sans-serif;
  padding: 20px;
  max-width: 800px;
  margin: auto;
  background: #f9f9f9;
  border-radius: 10px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
}

h1 {
  text-align: center;
  color: #2c3e50;
}

label {
  font-weight: bold;
  margin-top: 10px;
  display: block;
}

input {
  width: 100%;
  margin: 5px 0 15px;
  padding: 10px;
  border: 1px solid #ccc;
  border-radius: 5px;
  font-size: 14px;
}

.save-button {
  display: block;
  background-color: #27ae60;
  color: white;
  border: none;
  padding: 10px 15px;
  font-size: 16px;
  border-radius: 5px;
  cursor: pointer;
  margin: 20px auto 0;
  transition: background-color 0.3s ease;
}

.save-button:hover {
  background-color: #2ecc71;
}

.error {
  color: #e74c3c;
  text-align: center;
}
</style>
