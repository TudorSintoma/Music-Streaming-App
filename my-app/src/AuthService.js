class AuthService {
    static instance;

    constructor() {
        if (AuthService.instance) {
            return AuthService.instance;
        }
        AuthService.instance = this;
        this.user = null;
    }

    getUser() {
        return this.user;
    }

    setUser(userData) {
        this.user = userData;
    }

    async login(email, password) {
        try {
            const response = await fetch("http://localhost:8080/login", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                },
                body: JSON.stringify({ email, password }),
            });

            if (!response.ok) {
                throw new Error("Login failed");
            }

            const data = await response.json();

            if (data && data.userId) {

                console.log("Login successful", data);
                this.setUser(data);
                return data;
            } else {
                throw new Error("Invalid credentials or user not found");
            }
        } catch (error) {
            console.error("Error during login:", error);
            throw error;
        }
    }

    async forgotPassword(email) {
        try {
            const response = await fetch("http://localhost:8080/api/password-reset/request", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                },
                body: JSON.stringify({ email }),
            });

            if (!response.ok) {
                throw new Error("Password reset request failed");
            }

            const data = await response.json();
            console.log("Password reset requested:", data);
            return data;
        } catch (error) {
            console.error("Error during password reset request:", error);
            throw error;
        }
    }
    async resetPassword(token, newPassword) {
        try {
            const response = await fetch("http://localhost:8080/api/password-reset/confirm", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                },
                body: JSON.stringify({ token, newPassword }),
            });

            const data = await response.json();

            if (!response.ok) {
                const errorMsg = data?.message || "Reset request failed.";
                throw new Error(errorMsg);
            }

            return data;
        } catch (error) {
            console.error("Error during password reset:", error);
            throw error;
        }
    }

    async getToken (email, password) {
        const response = await fetch('http://localhost:8080/token', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ email, password }),
        });

        if (!response.ok) {
        throw new Error('Failed to retrieve token');
        }

        const data = await response.json();
        return data;
    }

    async getUserRole (userId) {
        try {
            const response = await fetch(`http://localhost:8080/users/${userId}/role`);
            if (!response.ok) {
            throw new Error('Failed to fetch user role');
        }
        return await response.json();
        } catch (error) {
            console.error('Error fetching user role:', error);
            return null;
        }
    }
}

const authService = new AuthService();
export default authService;
