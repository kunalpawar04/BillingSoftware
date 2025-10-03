import axios from "axios";

const API_URL = "http://localhost:8080/api/v1.0/payments";

export const createStripeOrder = async (data) => {
    // Calls backend to create a Checkout Session
    console.log("JWT token being sent:", localStorage.getItem("token"));
    return axios.post(`${API_URL}/create-checkout-session`, data, {
        headers: {
            'Authorization': `Bearer ${localStorage.getItem('token')}`
        }
    });
};

export const verifyPayment = async (data) => {
    return axios.post(`${API_URL}/verify`, data);
};
