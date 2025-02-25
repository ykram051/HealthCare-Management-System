<template>
  <div class="login-form">
    <h2>Login as {{ role }}</h2>
    <form @submit.prevent="handleLogin">
      <input v-model="credentials.email" type="email" placeholder="Email" required />
      <input v-model="credentials.password" type="password" placeholder="Password" required />
      <button type="submit">Login</button>
    </form>
    <p v-if="errorMessage" class="error">{{ errorMessage }}</p>
    <p>
      Don't have an account?
      <router-link :to="{ name: 'Register', query: { role } }">Register here</router-link>
    </p>
  </div>
</template>

<script>
import authService from "@/services/AuthService";

export default {
  data() {
    return {
      credentials: {
        email: "",
        password: "",
      },
      errorMessage: "",
      role: "",
    };
  },
  mounted() {
    this.role = this.$route.query.role || "student";
  },
  methods: {
    async handleLogin() {
      try {
        // Step 1: Log in and get user details (token already saved in AuthService)
        const { user, role } = await authService.login(this.credentials);

        console.log("User logged in:", user, role);

        // Step 2: Redirect based on the role returned by the backend
        if (role === "student") {
          this.$router.push("/student-dashboard");
        } else if (role === "health-staff") {
          this.$router.push("/staff-dashboard");
        } else {
          this.$router.push("/home");
        }
      } catch (error) {
        console.error("Login error:", error.message);
        this.errorMessage = error.message || "Login failed.";
      }
    },
  },
};
</script>
<style scoped>
/* General Layout */
.login-form {
  max-width: 400px;
  margin: 50px auto;
  padding: 20px 30px;
  border-radius: 10px;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);
  background-color: #f9f9f9;
  font-family: Arial, sans-serif;
  text-align: center;
}

/* Heading */
.login-form h2 {
  margin-bottom: 20px;
  color: #004e89;
  font-size: 24px;
  font-weight: bold;
}

/* Input Fields */
.login-form input {
  width: 100%;
  padding: 12px 15px;
  margin: 10px 0;
  font-size: 16px;
  border: 1px solid #ccc;
  border-radius: 5px;
  box-sizing: border-box;
}

.login-form input:focus {
  border-color: #004e89;
  outline: none;
  box-shadow: 0 0 5px rgba(0, 78, 137, 0.3);
}

/* Button */
.login-form button {
  width: 100%;
  padding: 12px;
  font-size: 16px;
  font-weight: bold;
  background-color: #004e89;
  color: white;
  border: none;
  border-radius: 5px;
  cursor: pointer;
  transition: background-color 0.3s ease;
}

.login-form button:hover {
  background-color: #003e6f;
}

/* Error Message */
.error {
  margin-top: 10px;
  color: #e74c3c;
  font-size: 14px;
}

/* Link to Register */
.login-form p {
  margin-top: 15px;
  font-size: 14px;
  color: #555;
}

.login-form p a {
  color: #004e89;
  text-decoration: none;
  font-weight: bold;
}

.login-form p a:hover {
  text-decoration: underline;
}
</style>
