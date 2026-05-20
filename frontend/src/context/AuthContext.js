import React, { createContext, useState, useContext, useEffect } from 'react';
import { authAPI } from '../services/api';

// Create the context
const AuthContext = createContext(null);

// Provider wraps the whole app — any component can access auth state
export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);

  // On app load, check if user is already logged in (token in localStorage)
  useEffect(() => {
    const storedUser = localStorage.getItem('user');
    const token = localStorage.getItem('token');
    if (storedUser && token) {
      setUser(JSON.parse(storedUser));
    }
    setLoading(false);
  }, []);

  const login = async (email, password) => {
    const response = await authAPI.login({ email, password });
    const { token, name, userId } = response.data;

    // Save to localStorage so user stays logged in on refresh
    localStorage.setItem('token', token);
    localStorage.setItem('user', JSON.stringify({ name, email, userId }));
    setUser({ name, email, userId });
    return response.data;
  };

  const register = async (name, email, password) => {
    const response = await authAPI.register({ name, email, password });
    const { token, userId } = response.data;

    localStorage.setItem('token', token);
    localStorage.setItem('user', JSON.stringify({ name, email, userId }));
    setUser({ name, email, userId });
    return response.data;
  };

  const logout = () => {
    localStorage.removeItem('token');
    localStorage.removeItem('user');
    setUser(null);
  };

  return (
    <AuthContext.Provider value={{ user, login, register, logout, loading }}>
      {children}
    </AuthContext.Provider>
  );
};

// Custom hook for easy access in any component
export const useAuth = () => useContext(AuthContext);
