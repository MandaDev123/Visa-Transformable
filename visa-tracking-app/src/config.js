// Configuration centralisée de l'API
// Utilisez l'IP de l'ordinateur pour que le téléphone puisse accéder au serveur
const API_BASE_URL = import.meta.env.VITE_API_URL || 'http://10.102.170.134:8080';

export default API_BASE_URL;
