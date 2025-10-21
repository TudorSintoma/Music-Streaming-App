import React, { useState, useEffect } from "react";
import "./index.css";
import {BrowserRouter, Route, Routes, useLocation, useSearchParams} from 'react-router-dom';
import backgroundImg from "./background.jpg";
import uploadImg from "./upload.jpg";
import loginBackgroundImg from "./login-background.jfif";
import createBackgroundImg from "./create-background.jpg";
import aboutBackgroundImg from "./about-background.jpg";
import contactBackgroundImg from "./contact-background.jpg";
import selectBackgroundImg from "./select-background.jpg";
import defaultAvatar from './default-avatar.png';
import userMainImg from "./userMain.jpg";
import discoveryBackgroundImg from './firstpage.jpg';
import passwordImg from "./password.jpg";
import ListenerImage from './Listener.png';  
import ArtistImage from './Artist.png';  
import userRepository from "./UserRepository";
import AuthService from "./AuthService";
import handleApiError from './handleApiError';
import { fetchGenres, fetchFilteredArtists, searchArtistsByName } from "./discoveryService";
import Modal from 'react-modal';

function App() {
  const [currentPage, setCurrentPage] = useState("home");
  const [userData, setUserData] = useState({
    fullName: "",
    email: "",
    password: "",
    accountType: "",
    genre: "",
    influencedArtists: [],
    likedArtists: []
  });
  let isReset = false;

    useEffect(() => {
        Modal.setAppElement('#root');
    }, []);

  const navigateTo = (page) => {
    setCurrentPage(page);
  };

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setUserData({
      ...userData,
      [name]: value
    });
  };

  const handleFormSubmit = (e) => {
    e.preventDefault();
    if (currentPage === "createAccount") {
      navigateTo("accountType");
    }
  };

  const handleAccountTypeSubmit = (e) => {
    e.preventDefault();
    if (userData.accountType === "artist" && userData.genre && userData.influencedArtists.length === 3) {
      navigateTo("home");
    } else if (userData.accountType === "listener" && userData.likedArtists.length === 3) {
      navigateTo("home");
    } else {
      alert("Please fill all required fields.");
    }
  };

  const Home = () => (
    <div
      className="home-container"
      style={{ backgroundImage: `url(${backgroundImg})` }}
    >
      <div className="left-section">
        <nav className="navbar">
          <button className="nav-button" onClick={() => navigateTo("home")}>
            Home
          </button>
          <button className="nav-button" onClick={() => navigateTo("about")}>
            About
          </button>
          <button className="nav-button" onClick={() => navigateTo("contact")}>
            Contact
          </button>
          <button className="nav-button" onClick={() => navigateTo("login")}>
            Login
          </button>
        </nav>
        <div className="content">
          <h1 className="title">Welcome to SoundWake</h1>
          <p className="description">
            We bring the underground to you. Discover new upcoming independent artists, create playlists
          </p>
          <p className="description">and share your experience with friends.</p>
        </div>
      </div>
    </div>
  );

  const AccountType = () => {
    const usr = userRepository.getCurrentUser();
    if (!usr || !usr.email) {
      return <div>Error: User is not logged in</div>; // Handle error if user is not found
    }
  
    const email = usr.email;

      const handleAccountTypeSubmit = async (type) => {
          if (type === "Artist") {
              userRepository.setCurrentAccountType("artist");
          } else {
              userRepository.setCurrentAccountType("listener");
          }

          const button = document.getElementById(type);
          if (button) button.disabled = true;

          try {
              const response = await fetch("http://localhost:8080/add-account-type", {
                  method: "POST",
                  headers: {
                      "Content-Type": "application/json",
                  },
                  body: JSON.stringify({ email, accountType: type }),
              });

              const rawText = await response.text();

              if (response.ok) {
                  const jsonData = JSON.parse(rawText);

                  const { token, userId, fullName, email: returnedEmail } = jsonData;
                  userRepository.setJwtToken(token);

                  if (type === "Artist") {
                      navigateTo("genre");
                  } else {
                      // create default playlists for listeners
                      const playlists = [
                          { name: "Liked Songs", imagePath: "/images/playlists/liked.png" },
                          { name: "Discovery Playlist", imagePath: "/images/playlists/discovery.png" },
                      ];

                      for (const playlist of playlists) {
                          try {
                              console.log(`Creating playlist: ${playlist.name}`);

                              const payload = {
                                  userId: usr.userId,
                                  name: playlist.name,
                                  imagePath: playlist.imagePath,
                              };

                              const playlistResponse = await fetch("http://localhost:8080/playlists", {
                                  method: "POST",
                                  headers: {
                                      "Content-Type": "application/json",
                                      "Authorization": `Bearer ${token}`,
                                  },
                                  body: JSON.stringify(payload),
                              });

                              const responseText = await playlistResponse.text();

                              if (playlistResponse.ok) {
                                  const createdPlaylist = JSON.parse(responseText);
                                  console.log(`Playlist created: ${createdPlaylist.name} (ID: ${createdPlaylist.id})`);

                                  if (playlist.name === "Liked Songs") {
                                      userRepository.setLikedPlaylistId(createdPlaylist.id);
                                      console.log(userRepository.getLikedPlaylistId());
                                  } else if (playlist.name === "Discovery Playlist") {
                                      userRepository.setDiscoveryPlaylistId(createdPlaylist.id);
                                  }
                              } else {
                                  console.error(`Failed to create playlist: ${playlist.name}`);
                                  console.error(`Status: ${playlistResponse.status}`);
                                  console.error(`Response: ${responseText}`);
                              }

                          } catch (error) {
                              console.error(`Error creating playlist: ${playlist.name}`);
                              console.error(error);
                          }
                      }

                      navigateTo("influences");
                  }

              } else {
                  alert(`Error: ${rawText}`);
              }
          } catch (error) {
              alert(`Error: ${error.message}`);
          } finally {
              if (button) button.disabled = false;
          }
      };

      return (
      <div
        style={{
          display: "flex",
          flexDirection: "column",
          justifyContent: "center",
          alignItems: "center",
          height: "100vh",
          backgroundImage: `url(${selectBackgroundImg})`,
          backgroundSize: "cover",
          backgroundPosition: "center",
          textAlign: "center",
        }}
      >
        {/* Title */}
        <h2 style={{ marginBottom: "40px", color: "white", fontSize: "28px" }}>
          Choose an account type (Listener or Artist)
        </h2>
  
        {/* Buttons */}
        <div
          style={{
            display: "flex",
            justifyContent: "space-between",
            width: "60%", // Adjust width to make sure the buttons fit well
            gap: "20px", // Add spacing between the buttons
          }}
        >
          <button
            id="Listener"
            style={{
              width: "300px", // 2x bigger button size
              height: "300px", // 2x bigger button size to make them square
              border: "none",
              background: "transparent",
              cursor: "pointer",
              transition: "transform 0.3s ease",
              display: "flex", // Center the image inside the button
              justifyContent: "center",
              alignItems: "center",
              borderRadius: "10px", // Optional: adds rounded corners to buttons
            }}
            onClick={() => handleAccountTypeSubmit("Listener")}
            onMouseOver={(e) => (e.currentTarget.style.transform = "scale(1.05)")}
            onMouseOut={(e) => (e.currentTarget.style.transform = "scale(1)")}
          >
            <img
              src={ListenerImage}
              alt="Listener"
              style={{
                width: "100%", // Make image fill the button's size
                height: "100%",
                objectFit: "cover", // Force image to cover the button area, making it square
                borderRadius: "10px", // Optional: rounds the image corners to match button
              }}
            />
          </button>
  
          <button
            id="Artist"
            style={{
              width: "300px", // 2x bigger button size
              height: "300px", // 2x bigger button size to make them square
              border: "none",
              background: "transparent",
              cursor: "pointer",
              transition: "transform 0.3s ease",
              display: "flex", // Center the image inside the button
              justifyContent: "center",
              alignItems: "center",
              borderRadius: "10px", // Optional: adds rounded corners to buttons
            }}
            onClick={() => handleAccountTypeSubmit("Artist")}
            onMouseOver={(e) => (e.currentTarget.style.transform = "scale(1.05)")}
            onMouseOut={(e) => (e.currentTarget.style.transform = "scale(1)")}
          >
            <img
              src={ArtistImage}
              alt="Artist"
              style={{
                width: "100%", // Make image fill the button's size
                height: "100%",
                objectFit: "cover", // Force image to cover the button area, making it square
                borderRadius: "10px", // Optional: rounds the image corners to match button
              }}
            />
          </button>
        </div>
      </div>
    );
  };

    const DiscoveryPage = () => {
        const [searchQuery, setSearchQuery] = useState('');
        const [genreNames, setGenreNames] = useState({});
        const [selectedGenre, setSelectedGenre] = useState('');
        const [selectedYearRange, setSelectedYearRange] = useState('');
        const [selectedPopularity, setSelectedPopularity] = useState('');
        const [orderBy, setOrderBy] = useState('');
        const [artists, setArtists] = useState([]);
        const [error, setError] = useState('');

        useEffect(() => {
            fetch('http://localhost:8080/genres/names')
                .then((res) => res.json())
                .then((data) => setGenreNames(data))
                .catch((err) => console.error('Failed to fetch genre names:', err));
        }, []);

        const handleSearchChange = (e) => {
            setSearchQuery(e.target.value);
        };

        const handleSearchSubmit = async (e) => {
            e.preventDefault();
            try {

                userRepository.setViewedFullName(searchQuery);

                navigateTo("artist");
            } catch (error) {
                console.error('Error during search:', error);
            }
        };

        const handleFilterSubmit = async () => {
            try {
                const payload = {};
                if (selectedGenre) payload.genreId = parseInt(selectedGenre);
                if (selectedYearRange) payload.yearCategory = selectedYearRange;
                if (selectedPopularity) payload.popularity = selectedPopularity;
                if (orderBy) payload.orderBy = orderBy;

                const token = userRepository.getJwtToken();

                const res = await fetch('http://localhost:8080/discovery/filter', {
                    method: 'POST',
                    headers: { 'Authorization': `Bearer ${token}`, 'Content-Type': 'application/json'},
                    body: JSON.stringify(payload),
                });

                if (!res.ok) throw new Error('Failed to fetch artists');

                const data = await res.json();
                setArtists(data);
            } catch (err) {
                setError('Failed to fetch artists.');
                console.error('Error filtering artists:', err);
            }
        };

        const handleArtistClick = (artist) => {
            userRepository.setViewedFullName(artist.fullName);
            navigateTo('artist');
        };

        const sharedInputStyle = {
            padding: '10px 20px',
            fontSize: '1rem',
            borderRadius: '20px',
            border: '2px solid #fff',
            outline: 'none',
            color: 'white',
            backgroundColor: 'rgba(0, 0, 0, 0.6)',
            transition: 'all 0.3s ease',
            margin: '5px',
        };

        return (
            <div
                style={{
                    backgroundImage: `url(${discoveryBackgroundImg})`,
                    backgroundSize: 'cover',
                    backgroundPosition: 'center',
                    height: '100vh',
                    display: 'flex',
                    flexDirection: 'column',
                    alignItems: 'center',
                    paddingTop: '40px',
                    color: 'white',
                    textAlign: 'center',
                    position: 'relative',
                    overflow: 'hidden',
                }}
            >
                <div
                    style={{
                        position: 'absolute',
                        top: 0,
                        left: 0,
                        right: 0,
                        bottom: 0,
                        backgroundColor: 'rgba(0, 0, 0, 0.4)',
                        zIndex: 1,
                    }}
                />

                {/* Back Button */}
                <button
                    onClick={() => navigateTo('userMain')}
                    style={{
                        position: 'absolute',
                        top: '20px',
                        left: '20px',
                        padding: '10px 20px',
                        backgroundColor: '#333',
                        color: '#fff',
                        border: 'none',
                        borderRadius: '5px',
                        cursor: 'pointer',
                        zIndex: 2,
                    }}
                >
                    Back
                </button>

                {/* Large Text in the Center */}
                <h1
                    style={{
                        fontSize: '4rem',
                        fontWeight: 'bold',
                        zIndex: 2,
                        letterSpacing: '2px',
                        textTransform: 'uppercase',
                        marginBottom: '20px',
                    }}
                >
                    Discovery Page
                </h1>

                {/* Search Bar Below the Text */}
                <div
                    style={{
                        zIndex: 2,
                        display: 'flex',
                        justifyContent: 'center',
                        width: '100%',
                        padding: '0 20px',
                    }}
                >
                    <input
                        type="text"
                        value={searchQuery}
                        onChange={handleSearchChange}
                        placeholder="Search artists, albums, or songs..."
                        style={{
                            padding: '15px 30px',
                            fontSize: '1.2rem',
                            width: '80%',
                            maxWidth: '600px',
                            borderRadius: '30px',
                            border: '2px solid #fff',
                            outline: 'none',
                            textAlign: 'center',
                            color: 'white',
                            backgroundColor: 'rgba(0, 0, 0, 0.6)', // Slightly transparent background
                            transition: 'all 0.3s ease',
                        }}
                    />
                </div>

                {/* Modern Search Button Below the Input */}
                <button
                    onClick={handleSearchSubmit}
                    style={{
                        marginTop: '15px',
                        padding: '15px 30px',
                        fontSize: '1.2rem',
                        backgroundColor: '#007BFF',
                        color: 'white',
                        border: 'none',
                        borderRadius: '30px',
                        cursor: 'pointer',
                        zIndex: 3, // Ensure the button is above the overlay
                        transition: 'all 0.3s ease',
                    }}
                    onMouseEnter={(e) => (e.target.style.backgroundColor = '#0056b3')}
                    onMouseLeave={(e) => (e.target.style.backgroundColor = '#007BFF')}
                >
                    Search
                </button>

                {/* Filter Section */}
                <div
                    style={{
                        display: 'flex',
                        flexWrap: 'wrap',
                        justifyContent: 'center',
                        zIndex: 2,
                        marginBottom: '15px',
                    }}
                >
                    <select style={sharedInputStyle} onChange={(e) => setSelectedGenre(e.target.value)}>
                        <option value="">Genre</option>
                        {Object.entries(genreNames).map(([id, name]) => (
                            <option key={id} value={id}>
                                {name}
                            </option>
                        ))}
                    </select>

                    <select style={sharedInputStyle} onChange={(e) => setSelectedYearRange(e.target.value)}>
                        <option value="">Debut Year</option>
                        <option value="BEFORE_1950">Before 1950</option>
                        <option value="1950_1970">1950-1970</option>
                        <option value="1970_1990">1970-1990</option>
                        <option value="1990_2010">1990-2010</option>
                        <option value="AFTER_2010">2010-Present</option>
                    </select>

                    <select style={sharedInputStyle} onChange={(e) => setSelectedPopularity(e.target.value)}>
                        <option value="">Popularity</option>
                        <option value="LOW">Low</option>
                        <option value="MEDIUM">Medium</option>
                        <option value="HIGH">High</option>
                    </select>

                    <select style={sharedInputStyle} onChange={(e) => setOrderBy(e.target.value)}>
                        <option value="">Order by</option>
                        <option value="NAME">Name</option>
                        <option value="DEBUT_YEAR">Debut Year</option>
                        <option value="POPULARITY">Popularity</option>
                    </select>

                    <button
                        onClick={handleFilterSubmit}
                        style={{
                            ...sharedInputStyle,
                            backgroundColor: '#28a745',
                            border: 'none',
                            cursor: 'pointer',
                        }}
                    >
                        Apply Filters
                    </button>
                </div>

                {/* Error Message */}
                {error && (
                    <div style={{ color: 'red', zIndex: 2, marginBottom: '10px' }}>{error}</div>
                )}

                {/* Artists List */}
                <div
                    style={{
                        zIndex: 2,
                        maxHeight: '300px',
                        overflowY: 'auto',
                        width: '80%',
                        maxWidth: '600px',
                        backgroundColor: 'rgba(0, 0, 0, 0.5)',
                        borderRadius: '10px',
                        padding: '20px',
                    }}
                >
                    {artists.length === 0 ? (
                        <p>No artists found.</p>
                    ) : (
                        artists.map((artist) => (
                            <div
                                key={artist.userId}
                                onClick={() => handleArtistClick(artist)}
                                style={{
                                    display: 'flex',
                                    alignItems: 'center',
                                    marginBottom: '15px',
                                    borderBottom: '1px solid white',
                                    paddingBottom: '10px',
                                    cursor: 'pointer',
                                }}
                            >
                                <img
                                    src={artist.imagePath || defaultAvatar}
                                    alt={artist.fullName}
                                    style={{
                                        width: '80px',
                                        height: '80px',
                                        objectFit: 'cover',
                                        borderRadius: '10px',
                                        marginRight: '20px',
                                    }}
                                />
                                <span style={{ fontSize: '1.2rem', fontWeight: '500' }}>
                {artist.fullName}
              </span>
                            </div>
                        ))
                    )}
                </div>
            </div>
        );
    };



    const ArtistPage = () => {
        const [artist, setArtist] = useState(null);
        const [topSongs, setTopSongs] = useState([]);
        const [albums, setAlbums] = useState([]);
        const [likedSongs, setLikedSongs] = useState(new Set());
        const [isFollowing, setIsFollowing] = useState(false);
        const [isModalOpen, setIsModalOpen] = useState(false);
        const [selectedAlbum, setSelectedAlbum] = useState(null);
        const [selectedAlbumSongs, setSelectedAlbumSongs] = useState([]);

        const currentUser = userRepository.getCurrentUser();
        const fullName = userRepository.getViewedFullName();
        const token = userRepository.getJwtToken();

        useEffect(() => {
            const fetchArtistData = async () => {
                try {
                    const response = await fetch(`http://localhost:8080/users/fullName/${encodeURIComponent(fullName)}`);
                    const data = await response.json();

                    if (data.error) {
                        console.error(data.error);
                        return;
                    }
                    setArtist(data);

                    // Fetch albums
                    const albumsResponse = await fetch(`http://localhost:8080/albums/user/${data.userId}`, {
                        headers: { 'Authorization': `Bearer ${token}` }
                    });
                    const albumData = await albumsResponse.json();
                    setAlbums(albumData);

                    // Fetch all songs from all albums
                    const allSongs = [];
                    for (const album of albumData) {
                        const songsResponse = await fetch(`http://localhost:8080/songs/album/${parseInt(album.id)}`, {
                            headers: { 'Authorization': `Bearer ${token}` }
                        });
                        const songs = await songsResponse.json();
                        // Attach album info to each song
                        const songsWithAlbum = songs.map(song => ({ ...song, album }));
                        allSongs.push(...songsWithAlbum);
                    }

                    // Sort and select top 5 songs by likes
                    const topFive = allSongs
                        .sort((a, b) => b.likes - a.likes)
                        .slice(0, 5);

                    setTopSongs(topFive);

                    const listenersResponse = await fetch(`http://localhost:8080/listeners/user/${currentUser.userId}`);
                    const listenersData = await listenersResponse.json();

                    const relatedArtistsResponse = await fetch(`http://localhost:8080/artists/user/${data.userId}`);
                    const relatedArtistsData = await relatedArtistsResponse.json();

                    // Check if current user is following the artist
                    const followResponse = await fetch(`http://localhost:8080/follows/check?listenerId=${listenersData.listenerId}&artistId=${relatedArtistsData.artistId}`, {
                        headers: { 'Authorization': `Bearer ${token}` }
                    });
                    const followData = await followResponse.json();
                    setIsFollowing(followData.isFollowing);

                    const likesResponse = await fetch(`http://localhost:8080/likes/user/${currentUser.userId}`, {
                        headers: { 'Authorization': `Bearer ${token}` }
                    });
                    const likesData = await likesResponse.json();

                    if (Array.isArray(likesData)) {
                        const likedSongIds = new Set(likesData.map(like => like.songId));
                        setLikedSongs(likedSongIds);

                        // Update the topSongs to reflect the liked state
                        setTopSongs((prevSongs) =>
                            prevSongs.map((song) => ({
                                ...song,
                                liked: likedSongIds.has(song.id),  // <-- Mark as liked if it's in the set
                            }))
                        );
                    } else {
                        console.error("Expected an array but received:", likesData);
                        setLikedSongs(new Set());
                    }


                } catch (error) {
                    console.error('Error fetching artist data:', error);
                }
            };

            if (fullName) {
                fetchArtistData();
            } else {
                console.log('No artist name found');
            }
        }, [fullName, currentUser.id]);

        const handleBackToDiscovery = () => {
            navigateTo("discovery");
        };

        const handleAlbumClick = async (album) => {
            try {
                const response = await fetch(`http://localhost:8080/songs/album/${album.id}`, {
                    headers: { 'Authorization': `Bearer ${token}` }
                });
                const songs = await response.json();

                // Initialize `liked` property based on `likedSongs` state
                const songsWithLikeState = songs.map((song) => ({
                    ...song,
                    liked: likedSongs.has(song.id),
                }));

                setSelectedAlbum(album);
                setSelectedAlbumSongs(songsWithLikeState);
                setIsModalOpen(true);
            } catch (error) {
                console.error('Error fetching album songs:', error);
            }
        };


        const handleFollowToggle = async () => {

            try {
                const listenersResponse = await fetch(`http://localhost:8080/listeners/user/${currentUser.userId}`);
                const listenersData = await listenersResponse.json();

                const relatedArtistsResponse = await fetch(`http://localhost:8080/artists/user/${artist.userId}`);
                const relatedArtistsData = await relatedArtistsResponse.json();

                const method = isFollowing ? 'DELETE' : 'POST';
                await fetch(`http://localhost:8080/follows`, {
                    method,
                    headers: { 'Content-Type': 'application/json', 'Authorization': `Bearer ${token}` },
                    body: JSON.stringify({
                        listenerId: listenersData.listenerId,
                        artistId: relatedArtistsData.artistId,
                    })
                });
                setIsFollowing(!isFollowing);
            } catch (error) {
                console.error('Error toggling follow:', error);
            }
        };

        const handleLike = async (songId, isLiked) => {
            try {
                const response = await fetch('http://localhost:8080/likes', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json', 'Authorization': `Bearer ${token}` },
                    body: JSON.stringify({
                        userId: currentUser.userId,
                        songId: songId,
                    }),
                });

                if (response.ok) {
                    const newLikeCount = await response.json();

                    // Update likedSongs Set
                    setLikedSongs((prevLikedSongs) => {
                        const updatedLikes = new Set(prevLikedSongs);
                        if (isLiked) {
                            updatedLikes.delete(songId);
                        } else {
                            updatedLikes.add(songId);
                        }
                        return updatedLikes;
                    });

                    // Update topSongs
                    setTopSongs((prevSongs) =>
                        prevSongs.map((song) =>
                            song.id === songId
                                ? { ...song, liked: !isLiked, likes: newLikeCount }
                                : song
                        )
                    );

                    // Update album modal songs
                    setSelectedAlbumSongs((prevSongs) =>
                        prevSongs.map((song) =>
                            song.id === songId ? { ...song, liked: !isLiked, likes: newLikeCount } : song
                        )
                    );

                    // If the song is being liked (not unliked), add it to the liked playlist
                    if (!isLiked) {
                        const likedPlaylistId = userRepository.getLikedPlaylistId();

                        if (likedPlaylistId) {
                            await fetch('http://localhost:8080/playlist-songs', {
                                method: 'POST',
                                headers: {
                                    'Content-Type': 'application/json',
                                    'Authorization': `Bearer ${token}`,
                                },
                                body: JSON.stringify({
                                    playlistId: likedPlaylistId,
                                    songId: songId,
                                }),
                            });
                        } else {
                            console.warn('Liked playlist ID not found in userRepository');
                        }
                    }
                } else {
                    console.error('Failed to like song');
                }
            } catch (error) {
                console.error('Error toggling like:', error);
            }
        };

        if (!artist) {
            return <div>Loading artist details...</div>;
        }

        const wallpaperUrl = artist.wallpaperPath
            ? encodeURI(artist.wallpaperPath)
            : discoveryBackgroundImg;

        return (
            <div
                className="artist-background"
                style={{
                    backgroundImage: `url("${wallpaperUrl}")`,
                    backgroundSize: 'cover',
                    backgroundRepeat: 'no-repeat',
                    backgroundPosition: 'center',
                    minHeight: '100vh',
                    width: '100vw',
                    position: 'relative',
                }}
            >
                <div
                    className="artist-overlay"
                    style={{
                        backgroundColor: 'rgba(0, 0, 0, 0.3)',
                        padding: '40px',
                        borderRadius: '10px',
                        maxWidth: '1900px',
                        margin: '0 auto',
                        position: 'relative',
                        top: '20%',
                        transform: 'translateY(0%)',
                        color: 'white',
                    }}
                >
                    {/* Back to Discovery Button */}
                    <button
                        onClick={handleBackToDiscovery}
                        style={{
                            padding: '12px 25px',
                            backgroundColor: 'rgba(255, 255, 255, 0.2)',
                            color: 'white',
                            border: 'none',
                            borderRadius: '5px',
                            fontSize: '1rem',
                            cursor: 'pointer',
                            marginBottom: '20px',
                        }}
                    >
                        Back to Discovery
                    </button>

                    {/* Artist Info */}
                    <div style={{ display: 'flex', alignItems: 'center', marginBottom: '30px', justifyContent: 'center' }}>
                        <img
                            src={artist.imagePath || defaultAvatar}
                            alt="Artist Icon"
                            style={{
                                width: '150px',
                                height: '150px',
                                objectFit: 'cover',
                                borderRadius: '50%',
                                marginRight: '20px',
                            }}
                        />
                    </div>
                    <div style={{ display: 'flex', alignItems: 'center', marginBottom: '30px', justifyContent: 'center' }}>
                        <h1>{artist.fullName}</h1>
                            <button
                                onClick={handleFollowToggle} className={`follow-button ${isFollowing ? 'following' : 'follow'}`}
                                style={{
                                    marginLeft: '20px',
                                    padding: '10px 20px',
                                    backgroundColor: isFollowing ? 'transparent' : '#1DB954',
                                    color: 'white',
                                    border: isFollowing ? '1px solid white' : 'none',
                                    borderRadius: '5px',
                                    cursor: 'pointer',
                                }}
                            >
                                {isFollowing ? 'Following' : 'Follow'}
                            </button>
                    </div>

                    <div
                        style={{
                            display: 'flex',
                            flexDirection: 'row',
                            flexWrap: 'wrap',
                            gap: '20px',
                            marginTop: '40px',
                            maxWidth: '1200px',
                            margin: '0 auto',
                        }}
                    >
                        {/* Top 5 Songs Section */}
                        <div
                            style={{
                                flex: '1 1 calc(50% - 10px)',
                                padding: '20px',
                                borderRadius: '10px',
                                minWidth: '300px',
                                justifyContent: "left",
                                backgroundColor: 'rgba(255, 255, 255, 0.05)'
                            }}
                        >
                            <h2>Top 5 Songs</h2>
                            <ul style={{ listStyleType: 'none', padding: 0 }}>
                                {topSongs.map((song) => (
                                    <li key={song.id} style={{ display: 'flex', alignItems: 'center', marginBottom: '15px' }}>
                                        <img
                                            src={song.album.imagePath}
                                            alt={song.album.name}
                                            style={{ width: '50px', height: '50px', objectFit: 'cover', marginRight: '15px' }}
                                        />
                                        <div style={{ flexGrow: 1 }}>
                                            <strong>{song.name}</strong>
                                            <div style={{ fontSize: '0.9em', color: '#ccc' }}>{song.album.name}</div>
                                        </div>
                                        <div style={{ display: 'flex', alignItems: 'center' }}>
                                            <button
                                                onClick={() => handleLike(song.id, song.liked)}
                                                style={{
                                                    background: 'none',
                                                    border: 'none',
                                                    cursor: 'pointer',
                                                    fontSize: '1.2em',
                                                    color: likedSongs.has(song.id) ? 'red' : 'white',
                                                    marginRight: '5px',
                                                }}
                                            >
                                                ♡
                                            </button>
                                            <span>{song.likes}</span>
                                        </div>
                                    </li>
                                ))}
                            </ul>
                        </div>

                        {/* Releases Section */}
                        <div
                            style={{
                                flex: '1 1 calc(50% - 10px)',
                                padding: '20px',
                                borderRadius: '10px',
                                minWidth: '300px',
                                justifyContent: "right",
                            }}
                        >
                            <h2>Releases</h2>
                            <div style={{ display: 'flex', flexWrap: 'wrap', gap: '20px' }}>
                                {albums.map((album) => (
                                    <div
                                        key={album.id}
                                        onClick={() => handleAlbumClick(album)}
                                        style={{
                                            cursor: 'pointer',
                                            width: '150px',
                                            textAlign: 'center',
                                        }}
                                    >
                                        <img
                                            src={album.imagePath}
                                            alt={album.name}
                                            style={{
                                                width: '150px',
                                                height: '150px',
                                                objectFit: 'cover',
                                            }}
                                        />
                                        <p>{album.name}</p>
                                        <p>{album.releaseYear}</p>
                                    </div>
                                ))}
                            </div>
                        </div>
                    </div>

                    {/* Artist Description */}
                    <div>
                        <h2>About</h2>
                        <p>{artist.description}</p>
                    </div>
                </div>
                <Modal
                    isOpen={isModalOpen}
                    onRequestClose={() => setIsModalOpen(false)}
                    contentLabel="Album Details"
                    style={{
                        content: {
                            top: '50%',
                            left: '50%',
                            right: 'auto',
                            bottom: 'auto',
                            marginRight: '-50%',
                            transform: 'translate(-50%, -50%)',
                            backgroundColor: '#222',
                            color: '#fff',
                            borderRadius: '10px',
                            padding: '20px',
                            width: '30%',
                            maxWidth: '600px',
                            maxHeight: "650px"
                        },
                        overlay: {
                            backgroundColor: 'rgba(0, 0, 0, 0.75)',
                        },
                    }}
                >
                    {selectedAlbum && (
                        <div>
                            <img
                                src={selectedAlbum.imagePath}
                                alt={selectedAlbum.name}
                                style={{ width: '100%', height: 'auto', borderRadius: '10px' }}
                            />
                            <h2 style={{ marginTop: '20px' }}>{selectedAlbum.name}</h2>
                            <p>{selectedAlbum.releaseYear}</p>
                            <ul style={{ listStyleType: 'none', padding: 0 }}>
                                {selectedAlbumSongs.map((song) => (
                                    <li key={song.id} style={{ display: 'flex', alignItems: 'center', marginBottom: '10px' }}>
                                        <div style={{ flexGrow: 1 }}>
                                            <strong>{song.name}</strong>
                                        </div>
                                        <div style={{ display: 'flex', alignItems: 'center' }}>
                                            <button
                                                onClick={() => handleLike(song.id, song.liked)}
                                                style={{
                                                    background: 'none',
                                                    border: 'none',
                                                    cursor: 'pointer',
                                                    fontSize: '1.2em',
                                                    color: likedSongs.has(song.id) ? 'red' : 'white',
                                                    marginRight: '5px',
                                                }}
                                            >
                                                ♡
                                            </button>
                                            <span>{song.likes}</span>
                                        </div>
                                    </li>
                                ))}
                            </ul>
                            <button
                                onClick={() => setIsModalOpen(false)}
                                style={{
                                    marginTop: '20px',
                                    padding: '10px 20px',
                                    backgroundColor: '#1DB954',
                                    color: 'white',
                                    border: 'none',
                                    borderRadius: '5px',
                                    cursor: 'pointer',
                                }}
                            >
                                Close
                            </button>
                        </div>
                    )}
                </Modal>
            </div>
        );
    };

  const Genre = () => {
    const [genres, setGenres] = useState([]);
    const [userType, setUserType] = useState("");
  
    const usr = userRepository.getCurrentUser();
    console.log(usr);
  
    useEffect(() => {
      if (usr && usr.accountType) {
        setUserType(usr.accountType);
      }
    }, [usr]);
  
    useEffect(() => {
      const fetchGenres = async () => {
        try {
          const response = await fetch("http://localhost:8080/genres");
          if (response.ok) {
            const data = await response.json();
            setGenres(data);
          } else {
            console.error("Failed to fetch genres.");
          }
        } catch (error) {
          console.error("Error fetching genres:", error);
        }
      };
  
      fetchGenres();
    }, []);
  
    const handleGenreSelect = async (genre) => {
      try {
          if (userType === "artist" && usr && usr.email) {
              const response = await fetch("http://localhost:8080/update-genre", {
                  method: "POST",
                  headers: {
                      "Content-Type": "application/json",
                  },
                  body: JSON.stringify({
                      email: usr.email,
                      genreId: genre.id,
                  }),
              });

              if (response.ok) {
                  console.log("Genre updated successfully");
                  navigateTo("influences");
              } else {
                  console.error("Failed to update genre.");
              }
          }
      } catch (error) {
        console.error("Error selecting genre:", error);
      }
    };
  
    const middleIndex = Math.ceil(genres.length / 2);
    const topRow = genres.slice(0, middleIndex);
    const bottomRow = genres.slice(middleIndex);
  
    return (
      <div
        className="genre-container"
        style={{
          backgroundImage: `url(${selectBackgroundImg})`,
          backgroundSize: "cover",
          backgroundPosition: "center",
          padding: "20px",
          display: "flex",
          flexDirection: "column",
          alignItems: "center",
          justifyContent: "center",
          minHeight: "100vh",
        }}
      >
        <h1
          style={{
            color: "white",
            fontSize: "36px",
            fontWeight: "bold",
            marginBottom: "20px",
          }}
        >
          Choose a genre
        </h1>
  
        {[topRow, bottomRow].map((row, rowIndex) => (
          <div
            key={rowIndex}
            style={{
              display: "flex",
              justifyContent: "center",
              gap: "20px",
              marginBottom: rowIndex === 0 ? "40px" : "0",
            }}
          >
            {row.map((genre) => (
              <button
                key={genre.id}
                className="genre-button"
                style={{
                  backgroundImage: `url(${genre.imagePath})`,
                  backgroundSize: "cover",
                  backgroundPosition: "center",
                  width: "200px",
                  height: "200px",
                  border: "none",
                  borderRadius: "10px",
                  cursor: "pointer",
                  transition: "transform 0.3s ease",
                }}
                onClick={() => handleGenreSelect(genre)}
                onMouseOver={(e) => (e.currentTarget.style.transform = "scale(1.1)")}
                onMouseOut={(e) => (e.currentTarget.style.transform = "scale(1)")}
              ></button>
            ))}
          </div>
        ))}
      </div>
    );
  };
  

  const Contact = () => {
    const [fullName, setFullName] = useState("");
    const [email, setEmail] = useState("");
    const [message, setMessage] = useState("");
  
    const handleFullNameChange = (event) => {
      setFullName(event.target.value);
    };
  
    const handleEmailChange = (event) => {
      setEmail(event.target.value);
    };
  
    const handleMessageChange = (event) => {
      setMessage(event.target.value);
    };
  
    return (
      <div
        className="contact-container"
        style={{ backgroundImage: `url(${contactBackgroundImg})` }}
      >
        <nav className="navbar-contact">
          <button className="nav-button" onClick={() => navigateTo("home")}>
            Home
          </button>
          <button className="nav-button" onClick={() => navigateTo("about")}>
            About
          </button>
          <button className="nav-button" onClick={() => navigateTo("contact")}>
            Contact
          </button>
          <button className="nav-button" onClick={() => navigateTo("login")}>
            Login
          </button>
        </nav>
  
        <div className="contact-content">
          {/* Left Section */}
          <div className="contact-left">
            <h1>Tell Us About Your Experience</h1>
            <p>
              We'd love to hear your thoughts! Connect with us on social media or
              fill out the contact form to share your feedback.
            </p>
            <div className="social-icons">
              <a href="https://facebook.com" target="_blank" rel="noopener noreferrer">
                <img src="/facebook.png" alt="Facebook" />
              </a>
              <a href="https://instagram.com" target="_blank" rel="noopener noreferrer">
                <img src="/instagram.png" alt="Instagram" />
              </a>
            </div>
          </div>
  
          {/* Right Section */}
          <div className="contact-form-container">
            <h2>Contact Form</h2>
            <form>
              <input
                type="text"
                placeholder="Full Name"
                value={fullName}
                onChange={handleFullNameChange}
                required
              />
              <input
                type="email"
                placeholder="Email"
                value={email}
                onChange={handleEmailChange}
                required
              />
              <textarea
                placeholder="Message"
                value={message}
                onChange={handleMessageChange}
                required
              ></textarea>
              <button type="submit">Submit</button>
            </form>
          </div>
        </div>
      </div>
    );
  };

  const About = () => (
    <div
      className="about-container"
      style={{ backgroundImage: `url(${aboutBackgroundImg})` }}
    >
      <nav className="navbar-about">
        <button className="nav-about-button" onClick={() => navigateTo("home")}>
          Home
        </button>
        <button className="nav-about-button" onClick={() => navigateTo("about")}>
          About
        </button>
        <button className="nav-about-button" onClick={() => navigateTo("contact")}>
          Contact
        </button>
        <button className="nav-about-button" onClick={() => navigateTo("login")}>
          Login
        </button>
      </nav>
      <div className="about-text">
        <h1>About SoundWake</h1>
        <p>
          SoundWake is your ultimate destination for discovering new music and
          creating personalized playlists. Whether you're exploring new genres or
          revisiting old favorites, SoundWake helps you connect with music in a
          whole new way.
        </p>
        <p>
          Share your playlists with friends, discover curated collections, and
          let SoundWake be the soundtrack to your life. Join a community of
          music lovers and start your journey with us today.
        </p>
      </div>
    </div>
  );

    const Login = () => {
        const [email, setEmail] = useState('');
        const [password, setPassword] = useState('');
        const [error, setError] = useState(''); // To handle error messages

        const handleEmailChange = (event) => {
            setEmail(event.target.value);
        };

        const handlePasswordChange = (event) => {
            setPassword(event.target.value);
        };

        const handleLoginSubmit = async (e) => {
            e.preventDefault();
            try {
                const response = await AuthService.login(email, password);

                if (response && response.userId) {
                    userRepository.setCurrentUser(response);
                    console.log("Logged in user:", response);

                    const roleResponse = await AuthService.getUserRole(response.userId);
                    if (roleResponse && roleResponse.role) {
                        userRepository.setCurrentAccountType(roleResponse.role);
                        console.log("User role set to:", roleResponse.role);
                    } else {
                        console.warn("User role not found.");
                    }

                    const tokenResponse = await AuthService.getToken(email, password);
                    if (tokenResponse && tokenResponse.token) {
                        userRepository.setJwtToken(tokenResponse.token);
                        console.log("JWT token set.");
                    }

                    navigateTo("userMain");
                } else {
                    setError('Invalid credentials or user not found');
                }
            } catch (error) {
                console.error("Error during login:", error);
                setError('Login failed. Please try again.');
            }
        };

        return (
            <div
                className="login-container"
                style={{
                    backgroundImage: `url(${loginBackgroundImg})`,
                    backgroundSize: 'cover',
                    backgroundPosition: 'center',
                }}
            >
                <div className="login-form-container">
                    <h1>Login</h1>
                    <form onSubmit={handleLoginSubmit}>
                        <input
                            type="email"
                            placeholder="Email"
                            value={email}
                            onChange={handleEmailChange}
                            required
                        />
                        <input
                            type="password"
                            placeholder="Password"
                            value={password}
                            onChange={handlePasswordChange}
                            required
                        />
                        <button type="submit">Login</button>
                    </form>
                    {error && <p className="error-message">{error}</p>}

                    <button onClick={() => navigateTo('home')} className="back-button">
                        Back to Home
                    </button>

                    <p className="create-account-option">
                        Don't have an account yet?{" "}
                        <span
                            className="create-account-link"
                            onClick={() => navigateTo('createAccount')}
                        >
                        Create one
                    </span>
                    </p>

                    <p className="forgot-password-option">
                        Forgot your password?{" "}
                        <span
                            className="forgot-password-link"
                            onClick={() => navigateTo('forgotPassword')}
                        >
                        Reset it here
                    </span>
                    </p>
                </div>
            </div>
        );
    };

    const ForgotPassword = () => {
        const [email, setEmail] = useState("");
        const [message, setMessage] = useState("");
        const [error, setError] = useState("");

        const handleEmailChange = (event) => {
            setEmail(event.target.value);
        };

        const handleForgotPasswordSubmit = async (e) => {
            e.preventDefault();
            try {
                const response = await AuthService.forgotPassword(email);
                if (response && response.message) {
                    setMessage("Check your email for password reset instructions.");
                    setError("");
                } else {
                    setError("Email not found.");
                    setMessage("");
                }
            } catch (error) {
                console.error("Password reset failed:", error);
                setError("Something went wrong. Please try again.");
                setMessage("");
            }
        };

        return (
            <div
                className="forgot-password-container"
                style={{
                    backgroundImage: `url(${passwordImg})`,
                    backgroundSize: "cover",
                    backgroundPosition: "center",
                }}
            >
                <div className="forgot-password-form-container">
                    <h1>Forgot Password</h1>
                    <form onSubmit={handleForgotPasswordSubmit}>
                        <input
                            type="email"
                            placeholder="Enter your email"
                            value={email}
                            onChange={handleEmailChange}
                            required
                        />
                        <button type="submit">Send Reset Link</button>
                    </form>

                    {message && <p className="success-message">{message}</p>}
                    {error && <p className="error-message">{error}</p>}

                    <button onClick={() => navigateTo("login")} className="back-button">
                        Back to Login
                    </button>
                </div>
            </div>
        );
    };

    const ResetPassword = () => {
        const location = useLocation();
        const params = new URLSearchParams(location.search);
        const token = params.get("token");

        const [newPassword, setNewPassword] = useState("");
        const [confirmPassword, setConfirmPassword] = useState("");
        const [message, setMessage] = useState("");
        const [error, setError] = useState("");

        const handleSubmit = async (e) => {
            e.preventDefault();

            if (newPassword !== confirmPassword) {
                setError("Passwords do not match.");
                setMessage("");
                return;
            }

            try {
                const data = await AuthService.resetPassword(token, newPassword); // Already JSON

                if (data && data.success) {
                    console.log("Your password has been reset successfully.");
                    setError("");
                    isReset = true;
                    navigateTo("login");
                } else {
                    setError(data?.message || "Invalid or expired reset link.");
                    setMessage("");
                }
            } catch (err) {
                console.error("Password reset failed:", err);
                setError("Something went wrong. Please try again.");
                setMessage("");
            }
        };

        return (
            <div
                className="forgot-password-container"
                style={{
                    backgroundImage: `url(${passwordImg})`,
                    backgroundSize: "cover",
                    backgroundPosition: "center",
                }}
            >
                <div className="forgot-password-form-container">
                    <h1>Reset Password</h1>
                    <form onSubmit={handleSubmit}>
                        <input
                            type="password"
                            placeholder="New password"
                            value={newPassword}
                            onChange={(e) => setNewPassword(e.target.value)}
                            required
                        />
                        <input
                            type="password"
                            placeholder="Confirm new password"
                            value={confirmPassword}
                            onChange={(e) => setConfirmPassword(e.target.value)}
                            required
                        />
                        <button type="submit">Reset Password</button>
                    </form>

                    {message && <p className="success-message">{message}</p>}
                    {error && <p className="error-message">{error}</p>}
                </div>
            </div>
        );
    };


    const CreateAccount = () => {
        const [fullName, setFullName] = useState("");
        const [email, setEmail] = useState("");
        const [password, setPassword] = useState("");

        const handleFullNameChange = (event) => setFullName(event.target.value);
        const handleEmailChange = (event) => setEmail(event.target.value);
        const handlePasswordChange = (event) => setPassword(event.target.value);

        const handleSubmit = async (event) => {
            event.preventDefault();

            try {
                // 1. Create the account
                const response = await fetch("http://localhost:8080/create-account", {
                    method: "POST",
                    headers: {
                        "Content-Type": "application/json",
                    },
                    body: JSON.stringify({ fullName, email, password }),
                });

                if (!response.ok) {
                    throw new Error("Failed to create account");
                }

                const data = await response.json();
                const { userId, fullName: createdName, email: createdEmail } = data;

                // 2. Save user to userRepository
                userRepository.setCurrentUser({
                    userId,
                    fullName: createdName,
                    email: createdEmail,
                    password,
                });

                console.log("User created successfully:", data);
                navigateTo("accountType");
            } catch (error) {
                handleApiError(error);
            }
        };


        return (
            <div className="create-container" style={{ backgroundImage: `url(${createBackgroundImg})` }}>
                <div className="create-form-container">
                    <h1>Create Account</h1>
                    <form onSubmit={handleSubmit}>
                        <input
                            type="text"
                            placeholder="Full Name"
                            value={fullName}
                            onChange={handleFullNameChange}
                            required
                        />
                        <input
                            type="email"
                            placeholder="Email"
                            value={email}
                            onChange={handleEmailChange}
                            required
                        />
                        <input
                            type="password"
                            placeholder="Password"
                            value={password}
                            onChange={handlePasswordChange}
                            required
                        />
                        <button type="submit">Sign Up</button>
                    </form>
                    <button onClick={() => navigateTo("login")} className="back-button">
                        Back
                    </button>
                </div>
            </div>
        );
    };


    const InfluencesPage = () => {
        const [genres, setGenres] = useState([]);
        const [selectedCount, setSelectedCount] = useState(0);
        const [artists, setArtists] = useState([]);
        const [currentEntityId, setCurrentEntityId] = useState(null); // artistId or listenerId
        const [selectedGenreId, setSelectedGenreId] = useState(null);
        const [selectedArtistId, setSelectedArtistId] = useState(null);
        const usr = userRepository.getCurrentUser();
        const isArtist = usr?.role === 'artist';

        useEffect(() => {
            const fetchGenres = async () => {
                try {
                    const response = await fetch("http://localhost:8080/genres");
                    const data = await response.json();
                    setGenres(data);
                } catch (err) {
                    console.error("Failed to fetch genres", err);
                }
            };

            const fetchCurrentEntityId = async () => {
                if (!usr?.userId) return;

                const endpoint = isArtist
                    ? `http://localhost:8080/artists/user/${usr.userId}`
                    : `http://localhost:8080/listeners/user/${usr.userId}`;

                try {
                    const response = await fetch(endpoint);
                    const data = await response.json();
                    const id = isArtist ? data.artistId : data.listenerId;
                    setCurrentEntityId(id);
                } catch (err) {
                    console.error(`Failed to fetch ${isArtist ? 'artist' : 'listener'} for user`, err);
                }
            };

            fetchGenres();
            fetchCurrentEntityId();
        }, [usr, isArtist]);

        const handleGenreClick = async (genreId) => {
            try {
                const response = await fetch(`http://localhost:8080/artists/genre/${genreId}`);
                const data = await response.json();
                setArtists(data);
                setSelectedGenreId(genreId);
            } catch (err) {
                console.error("Failed to fetch artists by genre", err);
            }
        };

        const handleArtistClick = async (userId) => {
            if (!currentEntityId || selectedCount >= 3) return;

            try {
                const artistResponse = await fetch(`http://localhost:8080/artists/user/${userId}`);
                const artistData = await artistResponse.json();
                const artistId = artistData.artistId;

                const body = isArtist
                    ? {
                        artistId: currentEntityId,
                        influencedById: artistId,
                    }
                    : {
                        listenerId: currentEntityId,
                        artistId: artistId,
                    };

                const endpoint = isArtist
                    ? "http://localhost:8080/artist-influences"
                    : "http://localhost:8080/follows";

                const token = userRepository.getJwtToken();

                const postResponse = await fetch(endpoint, {
                    method: "POST",
                    headers: {
                        "Content-Type": "application/json",
                        "Authorization": `Bearer ${token}`
                    },
                    body: JSON.stringify(body),
                });


                if (postResponse.ok) {
                    setSelectedCount((prev) => prev + 1);
                    if (selectedCount + 1 === 3) {
                        navigateTo("userMain");
                    }
                } else {
                    console.error("Failed to submit influence/follow");
                }
            } catch (err) {
                console.error("Error processing influence/follow", err);
            }
        };

        const encodeImagePath = (path) => {
            if (typeof path === "string" && path) {
                try {
                    const cleaned = path.replace(/\+/g, ' ');
                    const decoded = decodeURIComponent(cleaned);
                    const parts = decoded.split('/').map(part => encodeURIComponent(part));
                    return `url(${parts.join('/')})`;
                } catch (err) {
                    console.error("Error encoding image path:", err);
                    return `url(${path})`;
                }
            }
            return "";
        };

        return (
            <div
                className="influences-container"
                style={{
                    backgroundImage: `url(${selectBackgroundImg})`,
                    backgroundSize: "cover",
                    backgroundPosition: "center",
                    minHeight: "100vh",
                    padding: "20px",
                    color: "white",
                }}
            >
                <h1 style={{ fontSize: "36px", fontWeight: "bold", marginBottom: "10px", color: "black" }}>
                    Pick 3 of your favourite artists
                </h1>
                <p style={{ marginBottom: "30px", color: "black" }}>Selected: {selectedCount}/3</p>

                {/* Genre buttons */}
                <div
                    style={{
                        display: "flex",
                        flexWrap: "wrap",
                        justifyContent: "center",
                        gap: "20px",
                        marginBottom: "40px",
                    }}
                >
                    {genres.map((genre) => (
                        <button
                            key={genre.id}
                            onClick={() => handleGenreClick(genre.id)}
                            style={{
                                backgroundImage: encodeImagePath(genre.imagePath),
                                backgroundSize: "cover",
                                backgroundPosition: "center",
                                width: "150px",
                                height: "150px",
                                borderRadius: "10px",
                                cursor: "pointer",
                                transition: "all 0.3s ease",
                                transform: selectedGenreId === genre.id ? "scale(0.95)" : "scale(1)",
                                border: selectedGenreId === genre.id ? "2px solid white" : "none",
                            }}
                        />
                    ))}
                </div>

                {/* Artist buttons */}
                {artists.filter(artist => artist.imagePath || artist.user?.imagePath).length > 0 && (
                    <div style={{ display: "flex", flexWrap: "wrap", justifyContent: "center", gap: "20px" }}>
                        {artists
                            .filter(artist => artist.imagePath || artist.user?.imagePath)
                            .map((artist, index) => {
                                const imagePath = artist.imagePath || artist.user?.imagePath;
                                const name = artist.fullName || "Unnamed Artist";
                                const uniqueKey = artist.userId || index;
                                const isSelected = selectedArtistId === artist.userId;

                                return (
                                    <div key={uniqueKey} style={{ textAlign: "center" }}>
                                        <button
                                            onClick={() => handleArtistClick(artist.userId)}
                                            style={{
                                                backgroundImage: encodeImagePath(imagePath),
                                                backgroundSize: "cover",
                                                backgroundPosition: "center",
                                                width: "120px",
                                                height: "120px",
                                                borderRadius: "10px",
                                                border: isSelected ? "3px solid white" : "none",
                                                transform: isSelected ? "scale(0.95)" : "scale(1)",
                                                cursor: "pointer",
                                                transition: "all 0.3s ease",
                                            }}
                                        />
                                        <p style={{ marginTop: "8px", color: "white", fontSize: "14px" }}>
                                            {name}
                                        </p>
                                    </div>
                                );
                            })}
                    </div>
                )}
            </div>
        );
    };


    const UserMainPage = () => {
        const [userData, setUserData] = useState(null);
        const [albums, setAlbums] = useState([]);
        const [selectedAlbum, setSelectedAlbum] = useState(null);
        const [selectedAlbumSongs, setSelectedAlbumSongs] = useState([]);
        const [isModalOpen, setIsModalOpen] = useState(false);
        const [relatedArtists, setRelatedArtists] = useState([]);
        const [userPlaylists, setUserPlaylists] = useState([]);
        const [selectedPlaylist, setSelectedPlaylist] = useState(null);
        const [playlistSongs, setPlaylistSongs] = useState([]);
        const [isPlaylistModalOpen, setIsPlaylistModalOpen] = useState(false);


        const fullName = decodeURIComponent(userRepository.getCurrentFullName());
        const currentUser = userRepository.getCurrentUser();
        console.log(userRepository.getLikedPlaylistId());
        const token = userRepository.getJwtToken();
        const isArtist = currentUser?.role === 'artist';

        useEffect(() => {
            const fetchUserData = async () => {
                try {
                    const response = await fetch(`http://localhost:8080/users/fullName/${fullName}`);
                    const data = await response.json();
                    if (data.error) {
                        console.error(data.error);
                        return;
                    }

                    setUserData(data);

                    if (isArtist) {
                        const albumsResponse = await fetch(`http://localhost:8080/albums/user/${data.userId}`, {
                            headers: { 'Authorization': `Bearer ${token}` }
                        });
                        const albumData = await albumsResponse.json();
                        setAlbums(albumData);

                        // Fetch artist influences
                        const artistResponse = await fetch(`http://localhost:8080/artists/user/${data.userId}`, {
                            headers: { 'Authorization': `Bearer ${token}` }
                        });
                        const artistData = await artistResponse.json();
                        const artistId = artistData.artistId;
                        console.log(artistId);

                        const influencesResponse = await fetch(`http://localhost:8080/artist-influences/artist/${artistId}`, {
                            headers: { 'Authorization': `Bearer ${token}` }
                        });
                        const influencesData = await influencesResponse.json();

                        const influenceArtists = await Promise.all(
                            influencesData.map(async (influence) => {
                                // Step 1: Fetch the artist data by artistId
                                const artistRes = await fetch(`http://localhost:8080/artists/${influence.influencedBy.artistId}`, {
                                    headers: { 'Authorization': `Bearer ${token}` }
                                });
                                const artist = await artistRes.json();

                                // Step 2: Use the artist.userId to fetch the associated user data
                                const userRes = await fetch(`http://localhost:8080/users/${artist.user.userId}`, {
                                    headers: { 'Authorization': `Bearer ${token}` }
                                });
                                const user = await userRes.json();

                                // Step 3: Combine or return the user object directly
                                return {
                                    fullName: user.fullName,
                                    imagePath: user.imagePath,
                                    userId: user.userId // Optional: for key or navigation
                                };
                            })
                        );

                        setRelatedArtists(influenceArtists);
                    } else {
                        // Fetch followed artists
                        const listenerResponse = await fetch(`http://localhost:8080/listeners/user/${data.userId}`, {
                            headers: { 'Authorization': `Bearer ${token}` }
                        });
                        const listenerData = await listenerResponse.json();
                        const listenerId = listenerData.listenerId;

                        // Fetch user playlists
                        const playlistsResponse = await fetch(`http://localhost:8080/playlists/user/${data.userId}`, {
                            headers: { 'Authorization': `Bearer ${token}` }
                        });
                        const playlistsData = await playlistsResponse.json();
                        setUserPlaylists(playlistsData);

                        const followsResponse = await fetch(`http://localhost:8080/follows/listener/${listenerId}`, {
                            headers: { 'Authorization': `Bearer ${token}` }
                        });
                        const followsData = await followsResponse.json();

                        const followedArtists = await Promise.all(
                            followsData.map(async (follow) => {
                                const artistRes = await fetch(`http://localhost:8080/artists/${follow.artist.artistId}`, {
                                    headers: { 'Authorization': `Bearer ${token}` }
                                });
                                const artist = await artistRes.json();

                                const userRes = await fetch(`http://localhost:8080/users/${artist.user.userId}`, {
                                    headers: { 'Authorization': `Bearer ${token}` }
                                });
                                const user = await userRes.json();

                                return {
                                    fullName: user.fullName,
                                    imagePath: user.imagePath,
                                    userId: user.userId
                                };
                            })
                        );

                        setRelatedArtists(followedArtists);
                    }
                } catch (error) {
                    console.error('Error fetching user data:', error);
                }
            };

            if (fullName) {
                fetchUserData();
            }
        }, [fullName, isArtist, token]);

        const handleAlbumClick = async (album) => {
            try {
                const response = await fetch(`http://localhost:8080/songs/album/${album.id}`, {
                    headers: { 'Authorization': `Bearer ${token}` }
                });
                const songs = await response.json();
                setSelectedAlbum(album);
                setSelectedAlbumSongs(songs);
                setIsModalOpen(true);
            } catch (error) {
                console.error('Error fetching album songs:', error);
            }
        };

        const handlePlaylistClick = async (playlist) => {
            try {
                const response = await fetch(`http://localhost:8080/playlist-songs/playlist/${playlist.id}`, {
                    headers: { 'Authorization': `Bearer ${token}` }
                });
                const data = await response.json();
                console.log('Fetched songs for playlist:', data);

                // Assuming backend returns songs with songName and albumImagePath directly
                const formattedSongs = data.map(song => ({
                    id: song.songId,
                    name: song.songName,
                    albumImage: song.albumImagePath
                }));

                setSelectedPlaylist(playlist);
                setPlaylistSongs(formattedSongs);
                setIsPlaylistModalOpen(true);
            } catch (error) {
                console.error('Error fetching playlist songs:', error);
            }
        };



        const navBtnStyle = {
            background: 'none',
            color: '#fff',
            border: 'none',
            padding: '10px',
            textAlign: 'left',
            width: '100%',
            cursor: 'pointer',
            fontSize: '16px',
        };

        if (!userData) return <div>Loading user details...</div>;

        return (
            <div style={{ display: 'flex', height: '100vh', overflow: 'hidden' }}>
                {/* Sidebar */}
                <aside style={{ width: '220px', background: '#1c1c1c', color: '#fff', padding: '1rem' }}>
                    <h2 style={{ color: '#61dafb' }}>Menu</h2>
                    <ul style={{ listStyle: 'none', padding: 0 }}>
                        <li><button onClick={() => navigateTo("home")} style={navBtnStyle}>Home</button></li>
                        {isArtist
                            ? <li><button onClick={() => navigateTo("upload")} style={navBtnStyle}>Upload</button></li>
                            : <li><button onClick={() => navigateTo("discovery")} style={navBtnStyle}>Search</button></li>
                        }
                        <li><button onClick={() => navigateTo("account")} style={navBtnStyle}>My Account</button></li>
                        <li><button onClick={() => navigateTo("login")} style={navBtnStyle}>Log Out</button></li>
                    </ul>
                </aside>

                {/* Main Area */}
                <main
                    style={{
                        flex: 1,
                        backgroundColor: '#121212',
                        color: '#fff',
                        display: 'flex',
                        flexDirection: 'column',
                        alignItems: 'center',
                        backgroundImage: `url(${userData.wallpaperPath || userMainImg})`,
                        backgroundSize: 'cover',
                        backgroundPosition: 'center',
                        backgroundRepeat: 'no-repeat',
                        position: 'relative',
                    }}
                >
                    <img
                        src={userData.imagePath || defaultAvatar}
                        alt="User"
                        style={{
                            width: '150px',
                            height: '150px',
                            objectFit: 'cover',
                            borderRadius: '50%',
                            position: 'absolute',
                            top: '20px',
                            right: '20px',
                            border: '3px solid #fff',
                        }}
                    />
                    <div style={{ marginTop: '200px', zIndex: 1, textAlign: 'center' }}>
                        <h1>{userData.fullName}</h1>
                    </div>

                    {/* Artist-specific albums */}
                    {isArtist && (
                        <div style={{
                            marginTop: '40px',
                            padding: '20px',
                            backgroundColor: 'rgba(0, 0, 0, 0.6)',
                            borderRadius: '10px',
                            width: '80%',
                            maxWidth: '1200px',
                        }}>
                            <h2 style={{ marginBottom: '20px' }}>My Albums</h2>
                            <div style={{ display: 'flex', flexWrap: 'wrap', gap: '20px' }}>
                                {albums.map(album => (
                                    <div
                                        key={album.id}
                                        onClick={() => handleAlbumClick(album)}
                                        style={{
                                            cursor: 'pointer',
                                            width: '150px',
                                            textAlign: 'center',
                                        }}
                                    >
                                        <img
                                            src={album.imagePath}
                                            alt={album.name}
                                            style={{
                                                width: '150px',
                                                height: '150px',
                                                objectFit: 'cover',
                                                borderRadius: '5px',
                                            }}
                                        />
                                        <p>{album.name}</p>
                                        <p>{album.releaseYear}</p>
                                    </div>
                                ))}
                            </div>
                        </div>
                    )}

                    {/* Listener-specific playlists */}
                    {!isArtist && userPlaylists.length > 0 && (
                        <div style={{
                            marginTop: '40px',
                            padding: '20px',
                            backgroundColor: 'rgba(0, 0, 0, 0.6)',
                            borderRadius: '10px',
                            width: '80%',
                            maxWidth: '1200px',
                        }}>
                            <h2 style={{ marginBottom: '20px' }}>My Playlists</h2>
                            <div style={{ display: 'flex', flexWrap: 'wrap', gap: '20px' }}>
                                {userPlaylists.map((playlist) => (
                                    <div
                                        key={playlist.id}
                                        onClick={() => handlePlaylistClick(playlist)}
                                        style={{ width: '150px', textAlign: 'center', cursor: 'pointer' }}
                                    >
                                        <img
                                            src={playlist.imagePath}
                                            alt={playlist.name}
                                            style={{
                                                width: '150px',
                                                height: '150px',
                                                objectFit: 'cover',
                                                borderRadius: '5px',
                                            }}
                                        />
                                        <p>{playlist.name}</p>
                                    </div>
                                ))}
                            </div>
                        </div>
                    )}

                    {/* Related Artists */}
                    <div style={{
                        marginTop: '40px',
                        padding: '20px',
                        backgroundColor: 'rgba(0, 0, 0, 0.6)',
                        borderRadius: '10px',
                        width: '80%',
                        maxWidth: '1200px',
                    }}>
                        <h2 style={{ marginBottom: '20px' }}>
                            {isArtist ? 'My Influences' : 'Artists I Follow'}
                        </h2>
                        <div style={{ display: 'flex', flexWrap: 'wrap', gap: '20px' }}>
                            {relatedArtists.map(artist => (
                                <div
                                    key={artist.artistId}
                                    style={{
                                        width: '150px',
                                        textAlign: 'center',
                                    }}
                                >
                                    <img
                                        src={artist.imagePath || defaultAvatar}
                                        alt={artist.fullName}
                                        style={{
                                            width: '150px',
                                            height: '150px',
                                            objectFit: 'cover',
                                            borderRadius: '5px',
                                        }}
                                    />
                                    <p>{artist.fullName}</p>
                                </div>
                            ))}
                        </div>
                    </div>
                </main>

                {/* Album Modal */}
                <Modal
                    isOpen={isModalOpen}
                    onRequestClose={() => setIsModalOpen(false)}
                    contentLabel="Album Songs"
                    style={{
                        content: {
                            top: '50%',
                            left: '50%',
                            right: 'auto',
                            bottom: 'auto',
                            marginRight: '-50%',
                            transform: 'translate(-50%, -50%)',
                            backgroundColor: '#222',
                            color: '#fff',
                            borderRadius: '10px',
                            padding: '20px',
                            width: '30%',
                            maxWidth: '600px',
                            maxHeight: "650px",
                            overflowY: 'auto',
                        },
                        overlay: {
                            backgroundColor: 'rgba(0, 0, 0, 0.75)',
                        },
                    }}
                >
                    {selectedAlbum && (
                        <div>
                            <img
                                src={selectedAlbum.imagePath}
                                alt={selectedAlbum.name}
                                style={{ width: '100%', height: 'auto', borderRadius: '10px' }}
                            />
                            <h2 style={{ marginTop: '20px' }}>{selectedAlbum.name}</h2>
                            <p>{selectedAlbum.releaseYear}</p>
                            <ul style={{ listStyleType: 'none', padding: 0 }}>
                                {selectedAlbumSongs.map((song) => (
                                    <li key={song.id} style={{ marginBottom: '10px' }}>
                                        <strong>{song.name}</strong>
                                    </li>
                                ))}
                            </ul>
                            <button
                                onClick={() => setIsModalOpen(false)}
                                style={{
                                    marginTop: '20px',
                                    padding: '10px 20px',
                                    backgroundColor: '#1DB954',
                                    color: 'white',
                                    border: 'none',
                                    borderRadius: '5px',
                                    cursor: 'pointer',
                                }}
                            >
                                Close
                            </button>
                        </div>
                    )}
                </Modal>
                <Modal
                    isOpen={isPlaylistModalOpen}
                    onRequestClose={() => setIsPlaylistModalOpen(false)}
                    contentLabel="Playlist Songs"
                    style={{
                        content: {
                            top: '50%',
                            left: '50%',
                            right: 'auto',
                            bottom: 'auto',
                            marginRight: '-50%',
                            transform: 'translate(-50%, -50%)',
                            backgroundColor: '#222',
                            color: '#fff',
                            borderRadius: '10px',
                            padding: '20px',
                            width: '40%',
                            maxWidth: '700px',
                            maxHeight: "650px",
                            overflowY: 'auto',
                        },
                        overlay: {
                            backgroundColor: 'rgba(0, 0, 0, 0.75)',
                        },
                    }}
                >
                    {selectedPlaylist && (
                        <div>
                            <img
                                src={selectedPlaylist.imagePath}
                                alt={selectedPlaylist.name}
                                style={{ width: '100%', height: 'auto', borderRadius: '10px' }}
                            />
                            <h2 style={{ marginTop: '20px' }}>{selectedPlaylist.name}</h2>
                            <ul style={{ listStyleType: 'none', padding: 0 }}>
                                {playlistSongs.map((song) => (
                                    <li key={song.id} style={{ marginBottom: '15px', display: 'flex', alignItems: 'center' }}>
                                        <img
                                            src={song.albumImage}
                                            alt={song.name}
                                            style={{ width: '50px', height: '50px', objectFit: 'cover', borderRadius: '5px', marginRight: '10px' }}
                                        />
                                        <strong>{song.name}</strong>
                                    </li>
                                ))}
                            </ul>
                            <button
                                onClick={() => setIsPlaylistModalOpen(false)}
                                style={{
                                    marginTop: '20px',
                                    padding: '10px 20px',
                                    backgroundColor: '#1DB954',
                                    color: 'white',
                                    border: 'none',
                                    borderRadius: '5px',
                                    cursor: 'pointer',
                                }}
                            >
                                Close
                            </button>
                        </div>
                    )}
                </Modal>
            </div>
        );
    };

    const ArtistUploadPage = () => {

        const [albumName, setAlbumName] = useState('');
        const [releaseYear, setReleaseYear] = useState('');
        const [coverFile, setCoverFile] = useState(null);
        const [coverPath, setCoverPath] = useState('');
        const [songs, setSongs] = useState([{ name: '' }]);
        const [error, setError] = useState('');
        const [success, setSuccess] = useState('');

        const handleCoverUpload = async (e) => {
            const file = e.target.files[0];
            if (!file) return;

            const formData = new FormData();
            formData.append('file', file);

            try {
                const response = await fetch('http://localhost:8080/albums/upload-cover', {
                    method: 'POST',
                    headers: {
                        'Authorization': `Bearer ${userRepository.getJwtToken()}`
                    },
                    body: formData
                });

                if (!response.ok) throw new Error('Image upload failed');

                const imagePath = await response.text();
                setCoverPath(imagePath);
                setCoverFile(file);
            } catch (err) {
                console.error(err);
                setError('Failed to upload cover image.');
            }
        };

        const handleSongChange = (index, value) => {
            const newSongs = [...songs];
            newSongs[index].name = value;
            setSongs(newSongs);
        };

        const addSongField = () => {
            setSongs([...songs, { name: '' }]);
        };

        const removeSongField = (index) => {
            const newSongs = songs.filter((_, i) => i !== index);
            setSongs(newSongs);
        };

        const handleSubmit = async (e) => {
            e.preventDefault();
            setError('');
            setSuccess('');

            try {
                const formData = new FormData();
                formData.append("name", albumName);
                formData.append("userId", userRepository.getCurrentUser().userId);
                formData.append("releaseYear", releaseYear);
                formData.append("image", coverFile);
                formData.append("songs", JSON.stringify(songs));

                const albumResponse = await fetch("http://localhost:8080/albums/upload", {
                    method: "POST",
                    headers: {
                        Authorization: `Bearer ${userRepository.getJwtToken()}`,
                    },
                    body: formData,
                });

                if (!albumResponse.ok) throw new Error('Album creation failed');

                const album = await albumResponse.text();

                for (const song of songs) {
                    if (song.name.trim() !== '') {
                        await fetch('http://localhost:8080/songs', {
                            method: 'POST',
                            headers: {
                                'Content-Type': 'application/json',
                                'Authorization': `Bearer ${userRepository.getJwtToken()}`
                            },
                            body: JSON.stringify({
                                name: song.name,
                                album: { id: album.id },
                                likes: 0
                            })
                        });
                    }
                }

                setSuccess('Album and songs uploaded successfully!');
                setAlbumName('');
                setReleaseYear('');
                setCoverFile(null);
                setCoverPath('');
                setSongs([{ name: '' }]);
                navigateTo('userMain');

            } catch (err) {
                console.error(err);
                setError('Something went wrong. Please try again.');
            }
        };

        return (
            <div
                className="upload-container"
                style={{
                    backgroundImage: `url(${uploadImg})`,
                    backgroundSize: 'cover',
                    backgroundPosition: 'center',
                    minHeight: '100vh',
                    paddingTop: '50px',
                }}
            >
                <div className="upload-form-container">
                    <h1>Upload New Album</h1>
                    <form onSubmit={handleSubmit}>
                        <input
                            type="text"
                            placeholder="Album Name"
                            value={albumName}
                            onChange={(e) => setAlbumName(e.target.value)}
                            required
                        />

                        <input
                            type="number"
                            placeholder="Release Year"
                            value={releaseYear}
                            onChange={(e) => setReleaseYear(e.target.value)}
                            required
                        />

                        <input
                            type="file"
                            accept="image/*"
                            onChange={handleCoverUpload}
                            required
                        />
                        {coverFile && <p>Cover Uploaded: {coverFile.name}</p>}


                        <div
                            className="songs-section"
                            style={{
                                maxHeight: '200px',
                                overflowY: 'auto',
                                paddingBottom: '20px',
                                backgroundColor: 'rgba(255,255,255,0.7)',
                                borderRadius: '8px',
                                padding: '10px',
                                marginBottom: '20px'
                            }}
                        >
                            <label style={{ color: 'black', fontWeight: 'bold' }}>Songs:</label>
                            {songs.map((song, index) => (
                                <div key={index} className="song-input" style={{ marginBottom: '10px', display: 'flex', gap: '10px' }}>
                                    <input
                                        type="text"
                                        placeholder={`Song ${index + 1}`}
                                        value={song.name}
                                        onChange={(e) => handleSongChange(index, e.target.value)}
                                        required
                                        style={{ flex: 1 }}
                                    />
                                    {songs.length > 1 && (
                                        <button
                                            type="button"
                                            onClick={() => removeSongField(index)}
                                            style={{
                                                backgroundColor: '#ff4d4f',
                                                color: 'white',
                                                border: 'none',
                                                padding: '4px 6px',
                                                fontSize: '12px',
                                                width: '60px',
                                                borderRadius: '4px',
                                            }}
                                        >
                                            Remove
                                        </button>
                                    )}
                                </div>
                            ))}
                            <button type="button" onClick={addSongField} style={{ marginTop: '10px' }}>
                                Add Song
                            </button>
                        </div>

                        <button type="submit">Upload Album</button>
                    </form>

                    {error && <p className="error-message">{error}</p>}
                    {success && <p className="success-message">{success}</p>}

                    <button onClick={() => navigateTo('userMain')} className="back-button">
                        Back to Main Page
                    </button>
                </div>
            </div>
        );
    };

    const navBtnStyle = {
        background: 'transparent',
        border: 'none',
        color: '#fff',
        padding: '0.5rem 1rem',
        cursor: 'pointer',
        fontSize: '1rem',
        textAlign: 'left',
        width: '100%',
        marginBottom: '0.5rem',
    };

  return (
      <BrowserRouter>
          <div>
              {window.location.pathname === "/reset-password" && isReset === false && <ResetPassword />}
              {currentPage === "home" && window.location.pathname !== "/reset-password" && <Home />}
              {currentPage === "login" && <Login />}
              {currentPage === "upload" && <ArtistUploadPage />}
              {currentPage === "about" && <About />}
              {currentPage === "contact" && <Contact />}
              {currentPage === "createAccount" && <CreateAccount />}
              {currentPage === "accountType" && <AccountType />}
              {currentPage === "genre" && <Genre />}
              {currentPage === "discovery" && <DiscoveryPage />}
              {currentPage === "artist" && <ArtistPage />}
              {currentPage === "userMain" && <UserMainPage />}
              {currentPage === "influences" && <InfluencesPage />}
              {currentPage === "forgotPassword" && <ForgotPassword />}
          </div>
      </BrowserRouter>
  );
}

export default App;
