import axios from "axios";

const api = axios.create({
    baseURL: "http://localhost:8081/api", // Cambiado a localhost para desarrollo local
    timeout: 10000, // 10 segundos de timeout
    headers: {
        'Content-Type': 'application/json',
    }
});

// Interceptor para agregar token de autenticación
api.interceptors.request.use(
    (config) => {
        const token = localStorage.getItem('token');
        if (token) {
            config.headers.Authorization = `Bearer ${token}`;
        }
        console.log('🔧 API Request:', config.method?.toUpperCase(), config.url, 
                    token ? '✅ Con token' : '❌ Sin token');
        return config;
    },
    (error) => {
        console.error('❌ Error en request interceptor:', error);
        return Promise.reject(error);
    }
);

// Interceptor para manejar respuestas de error
api.interceptors.response.use(
    (response) => {
        console.log('✅ API Response:', response.status, response.config.url);
        return response;
    },
    (error) => {
        console.error('❌ API Error:', error.response?.status, error.response?.data);
        
        if (error.response?.status === 401) {
            console.warn('🔒 Token inválido o expirado, redirigiendo al login');
            localStorage.removeItem('token');
            localStorage.removeItem('user');
            window.location.href = '/login';
        }
        
        // Mejorar el manejo de errores
        const errorMessage = error.response?.data?.message || 
                           error.response?.data?.error || 
                           error.message || 
                           'Error desconocido';
        
        return Promise.reject(new Error(errorMessage));
    }
);

export default api;
