import  { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { Search, AlertCircle } from 'lucide-react';
import axios from 'axios';
import API_BASE_URL from '../config';

const SearchPage = () => {
  const [numero, setNumero] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [results, setResults] = useState([]);
  const navigate = useNavigate();

  const handleSearch = async (e) => {
    e.preventDefault();
    if (!numero.trim()) return;

    setLoading(true);
    setError('');
    setResults([]);

    try {
      const res = await axios.get(`${API_BASE_URL}/api/public/suivi/search?numero=${numero}`);
      if (res.data && res.data.length > 0) {
        if (res.data.length === 1) {
          // Redirect directly if only one result
          navigate(`/suivi/${res.data[0].id}`);
        } else {
          // Show list if multiple
          setResults(res.data);
        }
      } else {
        setError("Aucune demande trouvée pour ce numéro.");
      }
    } catch (err) {
      setError("Aucune demande trouvée ou erreur de connexion.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="glass-card">
      <div className="text-center mb-6">
        <h1>Suivi de Demande</h1>
        <p>Entrez votre numéro de passeport, numéro de visa ou carte résident pour suivre l'état de votre demande.</p>
      </div>

      <form className="search-form mb-6" onSubmit={handleSearch}>
        <div className="input-group">
          <Search className="input-icon" size={20} />
          <input
            type="text"
            className="glass-input"
            placeholder="N° Passeport, Visa ou Carte Résident"
            value={numero}
            onChange={(e) => setNumero(e.target.value)}
            required
          />
        </div>
        
        {error && (
          <div className="error-message">
            <AlertCircle size={18} />
            {error}
          </div>
        )}

        <button type="submit" className="btn btn-primary" disabled={loading}>
          {loading ? <div className="loading-spinner"></div> : 'Rechercher ma demande'}
        </button>
      </form>

      {results.length > 0 && (
        <div className="results-list">
          <h3 className="mb-4" style={{ fontSize: '1.1rem', color: 'var(--text-secondary)' }}>
            Plusieurs demandes trouvées :
          </h3>
          <div className="results-grid">
            {results.map((demande) => (
              <div 
                key={demande.id} 
                className="result-card"
                onClick={() => navigate(`/suivi/${demande.id}`)}
              >
                <div>
                  <div style={{ fontWeight: '600', marginBottom: '4px' }}>Demande #{demande.id}</div>
                  <div style={{ fontSize: '0.85rem', color: 'var(--text-secondary)' }}>
                    {demande.categorie} • {demande.dateDemande}
                  </div>
                </div>
                <div>
                  <span style={{
                    padding: '4px 10px', 
                    borderRadius: '50px', 
                    fontSize: '0.75rem', 
                    fontWeight: '600',
                    background: demande.statut === 'VISA_APPROUVE' ? 'rgba(34, 197, 94, 0.2)' : 'rgba(99, 102, 241, 0.2)',
                    color: demande.statut === 'VISA_APPROUVE' ? '#4ade80' : '#818cf8'
                  }}>
                    {demande.statut.replace('_', ' ')}
                  </span>
                </div>
              </div>
            ))}
          </div>
        </div>
      )}
    </div>
  );
};

export default SearchPage;
