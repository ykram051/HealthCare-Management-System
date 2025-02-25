const BASE_URL = 'http://localhost:8886';

const api = {
  async get(url) {
    try {
      const token = localStorage.getItem('token');
      if (!token) throw new Error('User is not logged in');

      const response = await fetch(`${BASE_URL}${url}`, {
        headers: {
          Authorization: token,
        },
      });

      if (!response.ok) {
        const error = await response.text(); 
        throw new Error(error || 'Request failed');
      }

      return await response.json(); 
    } catch (error) {
      throw new Error(error.message || 'Request failed');
    }
  },

  // POST for JSON bodies
  async post(url, body) {
    try {
      const token = localStorage.getItem('token');
      if (!token) throw new Error('User is not logged in');

      const response = await fetch(`${BASE_URL}${url}`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          Authorization: token,
        },
        body: JSON.stringify(body),
      });

      const rawResponse = await response.text();
      console.log("Raw API Response:", rawResponse);

      if (!response.ok) {
        throw new Error(rawResponse || 'Request failed');
      }

      return JSON.parse(rawResponse);
    } catch (error) {
      throw new Error(error.message || 'Request failed');
    }
  },

  // PUT for JSON bodies
  async put(url, body) {
    try {
      const token = localStorage.getItem('token');
      if (!token) throw new Error('User is not logged in');

      const response = await fetch(`${BASE_URL}${url}`, {
        method: 'PUT',
        headers: {
          'Content-Type': 'application/json',
          Authorization: token,
        },
        body: JSON.stringify(body),
      });

      const rawResponse = await response.text();

      if (!response.ok) {
        throw new Error(rawResponse || 'Request failed');
      }

      try {
        return JSON.parse(rawResponse);
      } catch {
        return rawResponse;
      }
    } catch (error) {
      throw new Error(error.message || 'Request failed');
    }
  },

  // DELETE request
  async delete(url) {
    try {
      const token = localStorage.getItem('token');
      if (!token) throw new Error('User is not logged in');

      const response = await fetch(`${BASE_URL}${url}`, {
        method: 'DELETE',
        headers: {
          Authorization: token,
        },
      });

      if (!response.ok) {
        const error = await response.text();
        throw new Error(error || 'Request failed');
      }

      return response.status === 204 ? {} : await response.json();
    } catch (error) {
      throw new Error(error.message || 'Request failed');
    }
  },

  // NEW: Upload (multipart/form-data) function
  async upload(url, formData) {
    try {
      const token = localStorage.getItem('token');
      if (!token) throw new Error('User is not logged in');

      const response = await fetch(`${BASE_URL}${url}`, {
        method: 'POST',
        headers: {
          Authorization: token,
        },
        body: formData,
      });

      const rawResponse = await response.text();
      console.log("Raw API Response (upload):", rawResponse);

      if (!response.ok) {
        throw new Error(rawResponse || 'Request failed');
      }

      return JSON.parse(rawResponse);
    } catch (error) {
      throw new Error(error.message || 'Request failed');
    }
  },
};

export default api;
