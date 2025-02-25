<template>
  <div class="landing-page">
    <div class="content-wrapper">
      <!-- Informative Section -->
      <div class="info-section">
        <h1>Welcome to the Health Center App</h1>
        <p>
          At UM6P Health Center, we are dedicated to providing excellent care
          and support for your health. Register or log in to access personalized
          services.
        </p>
        <ul>
          <li>✔ Expert medical staff</li>
          <li>✔ Book and manage appointments</li>
          <li>✔ Access health records</li>
        </ul>
      </div>

      <!-- Role Selection Section -->
      <div class="role-selection-container">
        <div class="role-selection">
          <div @click="selectRole('student')" class="role-option student-option">
            <h2>Student</h2>
          </div>
          <div @click="selectRole('staff')" class="role-option staff-option">
            <h2>Health Staff</h2>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  methods: {
    selectRole(role) {
      this.$router.push({ name: "Login", query: { role } });
    },
  },
};
</script>

<style scoped>
/* General styles for the landing page */
.landing-page {
  min-height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background-image: url('@/assets/back.avif');
  background-size: cover;
  background-repeat: no-repeat;
  background-position: center;
}

/* Content wrapper to split the layout */
.content-wrapper {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 80%;
  max-width: 1200px;
  padding: 40px;
  background: rgba(255, 255, 255, 0.85); /* Slightly opaque white */
  border-radius: 15px;
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.2);
}

/* Info Section Styling */
.info-section {
  width: 50%;
  padding: 20px;
}

.info-section h1 {
  font-size: 2.5rem;
  color: #004e89;
  margin-bottom: 20px;
}

.info-section p {
  font-size: 1.2rem;
  color: #555;
  margin-bottom: 20px;
  line-height: 1.6;
}

.info-section ul {
  list-style: none;
  padding: 0;
}

.info-section li {
  font-size: 1rem;
  margin-bottom: 10px;
  color: #333;
}

/* Role Selection Container Styling */
.role-selection-container {
  width: 40%;
  text-align: center;
}

.role-selection {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

/* Role option cards */
.role-option {
  background-color: white;
  border-radius: 15px;
  padding: 20px;
  box-shadow: 0 8px 15px rgba(0, 0, 0, 0.1);
  transition: transform 0.3s, box-shadow 0.3s;
  cursor: pointer;
}

.role-option:hover {
  transform: translateY(-10px);
  box-shadow: 0 12px 20px rgba(0, 0, 0, 0.2);
}

.role-option h2 {
  margin: 0;
  font-size: 1.2rem;
  color: #004e89;
}

/* Specific styles for each role */
.student-option {
  background: linear-gradient(135deg, #e3f2fd, #bbdefb);
}

.staff-option {
  background: linear-gradient(135deg, #ede7f6, #d1c4e9);
}

@media (max-width: 768px) {
  .content-wrapper {
    flex-direction: column;
    align-items: center;
  }

  .info-section,
  .role-selection-container {
    width: 100%;
    text-align: center;
  }

  .info-section {
    margin-bottom: 30px;
  }
}
</style>
