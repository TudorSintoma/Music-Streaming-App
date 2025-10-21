class UserRepository {
  constructor() {
    this.currentUser = null;
    this.viewedUser = null;
    this.jwtToken = null;
  }

  setCurrentUser(userData) {
    this.currentUser = userData;
  }

  getCurrentUser() {
    return this.currentUser;
  }

  clearCurrentUser() {
    this.currentUser = null;
  }

  isLoggedIn() {
    return this.currentUser !== null;
  }

  setCurrentAccountType(accountType) {
    if (this.currentUser) {
      this.currentUser.accountType = accountType;
    } else {
      this.currentUser = { accountType };
    }
  }

  getCurrentAccountType() {
    return this.currentUser?.accountType || null;
  }


  getLikedPlaylistId() {
    return this.currentUser?.likedPlaylistId || null;
  }

  setLikedPlaylistId(id) {
    if (!this.currentUser) this.currentUser = {};
    this.currentUser.likedPlaylistId = id;
  }

  setDiscoveryPlaylistId(id) {
    if (!this.currentUser) this.currentUser = {};
    this.currentUser.discoveryPlaylistId = id;
  }

  getDiscoveryPlaylistId() {
    return this.currentUser?.discoveryPlaylistId || null;
  }

  getCurrentFullName() {
    return this.currentUser ? this.currentUser.fullName : null;
  }

  setViewedUser(userData) {
    this.viewedUser = userData;
  }

  getViewedUser() {
    return this.viewedUser;
  }

  clearViewedUser() {
    this.viewedUser = null;
  }

  setViewedFullName(fullName) {
    if (this.viewedUser) {
      this.viewedUser.fullName = fullName;
    } else {
      this.viewedUser = { fullName };
    }
  }

  getViewedFullName() {
    return this.viewedUser ? this.viewedUser.fullName : null;
  }

  setJwtToken(token) {
    this.jwtToken = token;
    localStorage.setItem('jwtToken', token);
  }

  getJwtToken() {
    return this.jwtToken || localStorage.getItem('jwtToken');
  }
}

const userRepository = new UserRepository();
export default userRepository;
