<template>
  <h1>Or Upload Prescription As a PDF : </h1>
    <div class="upload-prescription">
      <form @submit.prevent="uploadPrescription">
        <label for="file">Select Prescription File (PDF):</label>
        <input
          ref="fileInput"
          type="file"
          id="file"
          accept="application/pdf"
          @change="handleFileChange"
          required
        />
        <button type="submit" :disabled="isLoading">
          {{ isLoading ? "Uploading..." : "Upload" }}
        </button>
      </form>
      <div v-if="errorMessage" class="error">{{ errorMessage }}</div>
      <div v-if="successMessage" class="success">{{ successMessage }}</div>
    </div>
  </template>
<script>
import api from "@/services/api";

export default {
  name: "PrescriptionUpload",
  data() {
    return {
      selectedFile: null,
      isLoading: false,
      errorMessage: "",
      successMessage: "",
    };
  },
  methods: {
    handleFileChange(event) {
      this.selectedFile = event.target.files[0];
    },
    async uploadPrescription() {
      if (!this.selectedFile) {
        this.errorMessage = "Please select a file.";
        return;
      }

      this.isLoading = true;
      this.errorMessage = "";
      this.successMessage = "";

      try {
        // Create FormData object for the file
        const formData = new FormData();
        formData.append("file", this.selectedFile);

        // Use the dedicated `upload` method in your `api` service
        // which handles the token and sets up the fetch request for multipart
        const response = await api.upload(
          "/api/students/me/treatments/upload",
          formData
        );

        this.successMessage = "Prescription uploaded successfully!";
        console.log("Server Response:", response);

        this.resetForm();
      } catch (error) {

        this.errorMessage =
          error.message || "Failed to upload prescription.";
        console.error("Error uploading prescription:", error.message);
      } finally {
        this.isLoading = false;
      }
    },
    resetForm() {
      this.selectedFile = null;
      if (this.$refs.fileInput) this.$refs.fileInput.value = null;
    },
  },
};
</script>


<style scoped>
.upload-prescription {
  max-width: 600px;
  margin: auto;
  padding: 20px;
  background-color: #f9f9f9;
  border-radius: 10px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
}

form {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

label {
  font-weight: bold;
  margin-bottom: 5px;
}

input[type="file"] {
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 5px;
  font-size: 14px;
}

button {
  background-color: #004e89;
  color: white;
  padding: 12px;
  border: none;
  border-radius: 5px;
  cursor: pointer;
  font-size: 16px;
  font-weight: bold;
  transition: background-color 0.3s ease;
}

button:hover {
  background-color: #003e6f;
}

button:disabled {
  background-color: #cccccc;
  cursor: not-allowed;
}

.error {
  color: #e74c3c;
  font-weight: bold;
}

.success {
  color: #27ae60;
  font-weight: bold;
}
</style>