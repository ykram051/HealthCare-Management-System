const BASE_URL = "http://localhost:8886/security/auth";

export const authService = {
  async login(credentials) {
    try {
      const response = await fetch(`${BASE_URL}/login`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(credentials),
      });

      if (!response.ok) {
        const errorMessage = await response.text();
        throw new Error(errorMessage || "Login failed");
      }

      const { token, user, role } = await response.json();
      console.log("Login successful. Token and user details received:", token, user, role);

      localStorage.setItem("token", `Bearer ${token}`);
      localStorage.setItem("user", JSON.stringify(user));
      localStorage.setItem("role", role);

      return { user, role };
    } catch (error) {
      console.error("Login error:", error.message);
      throw new Error(error.message || "Login failed");
    }
  },
  async register(role, user) {
    try {
      const endpoint = role === "student"
        ? "/register/student"
        : "/register/health-staff";
  

      const response = await fetch(`${BASE_URL}${endpoint}`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify(user),
      });
  
      if (!response.ok) {
        const errorMessage = await response.text();
        throw new Error(errorMessage || "Registration failed");
      }
  
      return await response.text(); 
    } catch (error) {
      console.error("Registration error:", error.message);
      throw new Error(error.message || "Registration failed");
    }
  }
  ,

  logout() {
    localStorage.removeItem("token");
    localStorage.removeItem("user");
    localStorage.removeItem("role");
    this.role = ""; 
    this.$router.push("/"); 
  },

  getRole() {
    return localStorage.getItem("role");
  },
};

export default authService;
