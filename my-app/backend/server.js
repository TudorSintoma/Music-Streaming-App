const express = require('express');
const { Client } = require('pg'); // For PostgreSQL
const cors = require('cors'); // Import CORS before using it
const app = express();

// Enable CORS to allow frontend access
app.use(cors({
  origin: 'http://localhost:3000', // Allow frontend on port 3002
  methods: 'GET,POST', // Allow specific methods
}));
app.options('*', cors()); // Handle preflight requests

// Enable JSON parsing for incoming requests
app.use(express.json());
// Initialize PostgreSQL client
const client = new Client({
  user: 'postgres',
  host: 'localhost',
  database: 'soundwake',
  password: 'Tudor',
  port: 5432,
});

// Connect to PostgreSQL
client.connect()
  .then(() => console.log('Connected to PostgreSQL database'))
  .catch((err) => console.error('Connection error', err.stack));

// Define the /create-account route
app.post('/create-account', async (req, res) => {
    const { fullName, email, password } = req.body;
  
    // Validate input fields
    if (!fullName || !email || !password) {
      return res.status(400).json({ error: 'All fields are required' });
    }
  
    try {
      // Log the request body for debugging purposes
      console.log('Request body:', req.body);
  
      // Query to insert the new user into the database
      const query = `
        INSERT INTO users (full_name, email, password)
        VALUES ($1, $2, $3)
        RETURNING user_id, full_name, email;`;
      const values = [fullName, email, password];
  
      // Execute the query
      await client.query(query, values);
  
      // If no errors occurred, respond with a success message
      console.log('User created successfully');
      res.status(201).json({ message: 'User created successfully' });
  
    } catch (err) {
      // Log any error that occurs during the process
      console.error('Error creating user:', err);
  
      // Respond with a generic error message
      res.status(500).json({ error: 'Internal server error' });
    }
  });
  

// Add account type (Artist or Listener)
app.post("/add-account-type", async (req, res) => {
    const { email, accountType } = req.body;
  
    try {
      // Get user ID based on email
      const userResult = await client.query("SELECT user_id FROM users WHERE email = $1", [email]);
      console.log("user:", userResult);
      if (userResult.rows.length === 0) {
        return res.status(404).json({ error: "User not found" }); // JSON response
      }
  
      const userId = userResult.rows[0].user_id;
  
      // Check account type and insert into the corresponding table
      if (accountType === "Artist") {
        await client.query("INSERT INTO artists (user_id) VALUES ($1)", [userId]);
      } else if (accountType === "Listener") {
        await client.query("INSERT INTO listeners (user_id) VALUES ($1)", [userId]);
      } else {
        return res.status(400).json({ error: "Invalid account type" }); // JSON response
      }
  
      // Send a success response as JSON
      return res.status(200).json({ message: `${accountType} added successfully` }); // Ensure this is JSON
    } catch (err) {
      console.error("Error adding account type:", err);
      return res.status(500).json({ error: "Failed to add account type" }); // JSON response
    }
});

// Get all genres with their image paths
app.get("/genres", async (req, res) => {
  try {
    const result = await client.query("SELECT genre_id, image_path FROM genres");
    const genres = result.rows;
    res.json(genres);
  } catch (error) {
    console.error(error);
    res.status(500).send("Error fetching genres.");
  }
});

// Start the server
const PORT = 5000;
app.listen(PORT, () => {
  console.log(`Server running on http://localhost:${PORT}`);
});

app.post("/update-genre", async (req, res) => {
  const { email, genreId } = req.body;

  try {
    // Get the user_id for the given email
    const userResult = await client.query(
      "SELECT user_id FROM users WHERE email = $1",
      [email]
    );

    if (userResult.rows.length === 0) {
      return res.status(404).json({ error: "User not found" });
    }

    const userId = userResult.rows[0].user_id;

    // Check if the user is an artist
    const artistResult = await client.query(
      "SELECT * FROM artists WHERE user_id = $1",
      [userId]
    );

    if (artistResult.rows.length === 0) {
      return res.status(404).json({ error: "Artist not found" });
    }

    // Update the genre_id for the artist
    await client.query(
      "UPDATE artists SET genre_id = $1 WHERE user_id = $2",
      [genreId, userId]
    );

    return res.status(200).json({ message: "Genre updated successfully" });
  } catch (err) {
    console.error("Error updating genre:", err);
    return res.status(500).json({ error: "Failed to update genre" });
  }
});

app.post("/login", async (req, res) => {
  const { email, password } = req.body;

  try {
    const result = await client.query(
      "SELECT user_id, full_name, email FROM users WHERE email = $1 AND password = $2",
      [email, password]
    );

    if (result.rows.length === 0) {
      return res.status(401).json({ error: "Invalid email or password" });
    }

    const user = result.rows[0];
    res.status(200).json({ user });
  } catch (error) {
    console.error("Error logging in:", error);
    res.status(500).json({ error: "Internal server error" });
  }
});

app.get('/artist/:name', async (req, res) => {
  const { name } = req.params;
  try {
    // Query the users table to find the artist by full_name
    const result = await client.query(
      "SELECT full_name, description, wallpaper_path, image_path FROM users WHERE full_name = $1",
      [name]
    );

    if (result.rows.length === 0) {
      return res.status(404).json({ error: "Artist not found" });
    }

    // Send back the artist's information
    const artist = result.rows[0];
    res.json(artist);
  } catch (error) {
    console.error("Error fetching artist data:", error);
    res.status(500).json({ error: "Failed to fetch artist data" });
  }
});
