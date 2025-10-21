
const handleApiError = (error) => {
    if (error && typeof error === 'object' && 'status' in error && 'message' in error) {
        const { status, message } = error;

        switch (status) {
            case 400:
                if (message.startsWith('Validation Errors')) {
                    alert(message);
                } else {
                    alert(`Validation Error: ${message}`);
                }
                break;

            case 404:
                alert(`Not Found: ${message}`);
                break;

            case 409:
                alert(`Conflict Error: ${message}`);
                break;

            case 500:
                alert(`Server Error: ${message}`);
                break;

            default:
                alert(`Unexpected Error: ${message}`);
                break;
        }
    } else if (error instanceof Error) {
        alert(`Error: ${error.message}`);
    } else {
        alert('An unexpected error occurred.');
    }
    console.error("API Error:", error);
};

export default handleApiError;
