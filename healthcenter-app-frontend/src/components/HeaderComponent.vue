<template>
    <header class="header">
      <img src="@/assets/logo.png" alt="UM6P Health Center Logo" class="logo" />
  
      <nav>
        <template v-if="role === 'student'">
          <router-link to="/student-profile" class="nav-link">My Profile</router-link>
          <router-link to="/student-dashboard" class="nav-link">Dashboard</router-link>
        </template>
        <template v-else-if="role === 'health-staff'">
          <router-link to="/staff-profile" class="nav-link">My Profile</router-link>
          <router-link to="/staff-dashboard" class="nav-link">Dashboard</router-link>
        </template>
        <router-link to="/" class="nav-link" @click="logout">Logout</router-link>
      </nav>
    </header>
  </template>
  
  <script>
  export default {
    name: "HeaderComponent",
    props: {
      role: {
        type: String,
        required: true, 
      },
    },
    methods: {
      logout() {
        localStorage.removeItem("token");
        localStorage.removeItem("user");
        localStorage.removeItem("role");
        this.$emit("logout");
      },
    },
  };
  </script>
  
  <style scoped>
  .header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 20px 40px;
    background-color: #004e89;
  }
  
  .logo {
    height: 80px;
    max-width: 100%;
  }
  
  nav {
    margin-left: auto;
  }
  
  .nav-link {
    font-size: 16px;
    color: white;
    text-decoration: none;
    margin-left: 20px;
    padding: 10px 15px;
    border-radius: 6px;
    transition: background-color 0.3s ease;
  }
  
  .nav-link:hover {
    background-color: #003e6f;
  }
  </style>
  