<template>
  <div id="app">
    <!-- Use the Header component -->
    <HeaderComponent :role="role" @logout="logout" />

    <!-- Main Content -->
    <router-view />
  </div>
</template>

<script>
import HeaderComponent from "@/components/HeaderComponent.vue";

export default {
  name: "App",
  components: {
    HeaderComponent,
  },
  data() {
    return {
      role: "", // User's role (student or health-staff)
    };
  },
  methods: {
    logout() {
      localStorage.removeItem("token");
      localStorage.removeItem("user");
      localStorage.removeItem("role");
      this.role = ""; // Clear the role
      this.$router.push("/"); // Redirect to the landing page
    },
  },
  mounted() {
    // Fetch the role from local storage
    const role = localStorage.getItem("role");
    console.log("Retrieved role from localStorage:", role); // Debug log

    if (role === "student" || role === "health-staff") {
      this.role = role;
    } else {
      // Redirect to the landing page if role is invalid or not set
      console.warn("Invalid or missing role. Redirecting to landing page.");
      this.$router.push("/");
    }
  },
};
</script>

<style>
/* General styles */
#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: center;
  color: #2c3e50;
}
</style>
