<template>
  <div class="register-form">
    <div class="register-header">
      <h2>Register as {{ role === "student" ? "Student" : "Health Staff" }}</h2>
      <p>Please fill in the details below to create your account.</p>
    </div>
    <form @submit.prevent="handleRegister" class="form-container">
      <div class="form-group">
        <label for="username">Username</label>
        <input
          v-model="user.username"
          id="username"
          type="text"
          placeholder="Enter your username"
          required
        />
      </div>
      <div class="form-group">
        <label for="email">Email</label>
        <input
          v-model="user.email"
          id="email"
          type="email"
          placeholder="Enter your email"
          required
        />
      </div>
      <div class="form-group">
        <label for="password">Password</label>
        <input
          v-model="user.password"
          id="password"
          type="password"
          placeholder="Enter your password"
          required
        />
      </div>
      <div v-if="role === 'student'" class="form-group">
        <label for="dateOfBirth">Date of Birth</label>
        <input
          v-model="user.dateOfBirth"
          id="dateOfBirth"
          type="date"
          required
        />
      </div>
      <div v-if="role === 'student'" class="form-group">
        <label for="address">Address</label>
        <input
          v-model="user.address"
          id="address"
          type="text"
          placeholder="Enter your address"
          required
        />
      </div>
      <div v-if="role === 'student'" class="form-group">
        <label for="allergies">Allergies</label>
        <input
          v-model="user.allergies"
          id="allergies"
          type="text"
          placeholder="Enter any allergies (if none, leave blank)"
        />
      </div>
      <button type="submit" class="register-button">Register</button>
    </form>
    <p v-if="successMessage" class="success-message">{{ successMessage }}</p>
    <p v-if="errorMessage" class="error-message">{{ errorMessage }}</p>
  </div>
</template>

<script>
import authService from "@/services/AuthService";

export default {
  data() {
    return {
      user: {
        username: "",
        email: "",
        password: "",
        dateOfBirth: "",
        address: "",
        allergies: "",
      },
      successMessage: "",
      errorMessage: "",
      role: "",
    };
  },
  mounted() {
    this.role = this.$route.query.role || "student";
  },
  methods: {
    async handleRegister() {
  try {
    // Convert allergies to an array if the role is student
    if (this.role === "student") {
      if (this.user.allergies) {
        this.user.allergies = this.user.allergies.split(",").map((item) => item.trim());
      } else {
        this.user.allergies = []; // Send an empty array if no allergies are provided
      }
    }

    // Format the dateOfBirth if present
    if (this.role === "student" && this.user.dateOfBirth) {
      const date = new Date(this.user.dateOfBirth);
      this.user.dateOfBirth = date.toISOString().split("T")[0];
    }

    const message = await authService.register(this.role, this.user);
    this.successMessage = message;
    this.$router.push("/login"); // Redirect to login on success
  } catch (error) {
    this.errorMessage = error.message || "Registration failed. Please try again.";
  }
}



  },
};
</script>

<style scoped>
/* General Layout */
.register-form {
  max-width: 500px;
  margin: 50px auto;
  background: #f9f9f9;
  padding: 20px;
  border-radius: 10px;
  box-shadow: 0px 4px 10px rgba(0, 0, 0, 0.1);
  font-family: Arial, sans-serif;
}

.register-header h2 {
  font-size: 24px;
  color: #004e89;
  text-align: center;
  margin-bottom: 10px;
}

.register-header p {
  font-size: 14px;
  color: #666;
  text-align: center;
  margin-bottom: 20px;
}

.form-container {
  display: flex;
  flex-direction: column;
}

.form-group {
  margin-bottom: 15px;
}

label {
  display: block;
  font-weight: bold;
  margin-bottom: 5px;
}

input {
  width: 100%;
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 5px;
  box-sizing: border-box;
  font-size: 14px;
}

input:focus {
  outline: none;
  border-color: #004e89;
  box-shadow: 0 0 5px rgba(0, 78, 137, 0.2);
}

.register-button {
  background: #004e89;
  color: white;
  border: none;
  padding: 12px;
  border-radius: 5px;
  font-size: 16px;
  cursor: pointer;
  transition: background 0.3s ease;
}

.register-button:hover {
  background: #003e6f;
}

/* Success and Error Messages */
.success-message {
  color: green;
  text-align: center;
  margin-top: 20px;
}

.error-message {
  color: red;
  text-align: center;
  margin-top: 20px;
}
</style>
