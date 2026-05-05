import { Routes, Route } from 'react-router-dom';
import SearchPage from './components/SearchPage';
import SuiviPage from './components/SuiviPage';

function App() {
  return (
    <div className="app-container">
      <nav className="navbar">
        <div className="navbar-brand">
          <span className="logo-icon">🛂</span>
          <span className="logo-text">VisaTrack</span>
        </div>
      </nav>
      <main className="main-content">
        <Routes>
          <Route path="/" element={<SearchPage />} />
          <Route path="/suivi/:id" element={<SuiviPage />} />
        </Routes>
      </main>
      <footer className="footer">
        <p>© {new Date().getFullYear()} VisaTrack - Ministère de l'Intérieur</p>
      </footer>
    </div>
  );
}

export default App;
