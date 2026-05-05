import  { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import axios from 'axios';
import { FileText, Scan, CheckCircle, ArrowLeft, Loader2 } from 'lucide-react';
import API_BASE_URL from '../config';

const SuiviPage = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const [demande, setDemande] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    const fetchDemande = async () => {
      try {
        const res = await axios.get(`${API_BASE_URL}/api/public/suivi/${id}`);
        setDemande(res.data);
      } catch (error) {
        setError("Demande introuvable ou erreur serveur.");
      } finally {
        setLoading(false);
      }
    };
    fetchDemande();
  }, [id]);

  if (loading) {
    return (
      <div className="glass-card" style={{ display: 'flex', justifyContent: 'center', padding: '4rem' }}>
        <Loader2 className="loading-spinner" style={{ width: '40px', height: '40px', color: 'var(--accent-color)' }} />
      </div>
    );
  }

  if (error || !demande) {
    return (
      <div className="glass-card text-center">
        <h2 className="mb-4" style={{ color: 'var(--danger)' }}>Erreur</h2>
        <p className="mb-6">{error}</p>
        <button className="btn btn-primary" onClick={() => navigate('/')}>Retour à la recherche</button>
      </div>
    );
  }

  const getStatusLevel = (status) => {
    switch (status) {
      case 'DOSSIER_CREE': return 1;
      case 'SCAN_TERMINE': return 2;
      case 'VISA_APPROUVE': return 3;
      default: return 0;
    }
  };

  const level = getStatusLevel(demande.statut);

  return (
    <div className="glass-card" style={{ maxWidth: '800px' }}>
      <div style={{ display: 'flex', alignItems: 'center', gap: '1rem', marginBottom: '2rem' }}>
        <button 
          onClick={() => navigate('/')} 
          style={{ background: 'transparent', border: 'none', color: 'var(--text-secondary)', cursor: 'pointer', display: 'flex', alignItems: 'center', gap: '4px' }}
        >
          <ArrowLeft size={20} /> Retour
        </button>
        <h1 style={{ fontSize: '1.75rem', margin: 0 }}>Suivi Demande #{demande.id}</h1>
      </div>

      <div className="detail-box mb-6">
        <div className="detail-grid">
          <div className="detail-item">
            <span className="detail-label">Demandeur</span>
            <span className="detail-val">{demande.nom} {demande.prenoms}</span>
          </div>
          <div className="detail-item">
            <span className="detail-label">Passeport</span>
            <span className="detail-val">{demande.numeroPasseport}</span>
          </div>
          <div className="detail-item">
            <span className="detail-label">Catégorie</span>
            <span className="detail-val">{demande.categorie?.replace(/_/g, ' ')}</span>
          </div>
          <div className="detail-item">
            <span className="detail-label">Date Demande</span>
            <span className="detail-val">{demande.dateDemande}</span>
          </div>
        </div>
      </div>

      <h2 className="mb-4" style={{ fontSize: '1.25rem' }}>État d'avancement</h2>
      
      <div className="timeline">
        {/* Step 1: Dossier Créé */}
        <div className={`timeline-step ${level >= 1 ? 'completed' : ''}`}>
          <div className="step-icon">
            <FileText size={20} />
          </div>
          <div className="step-content">
            <h3>Dossier Créé</h3>
            <p>La demande a été enregistrée dans le système.</p>
          </div>
        </div>

        {/* Step 2: Scan Terminé */}
        <div className={`timeline-step ${level >= 2 ? 'completed' : level === 1 ? 'active' : ''}`}>
          <div className="step-icon">
            <Scan size={20} />
          </div>
          <div className="step-content">
            <h3>Scan Terminé</h3>
            <p>Toutes les pièces justificatives ont été scannées et vérifiées.</p>
            {level === 1 && <p style={{ color: 'var(--warning)', marginTop: '8px', fontSize: '0.8rem' }}>En attente de la complétion des scans.</p>}
          </div>
        </div>

        {/* Step 3: Approuvé */}
        <div className={`timeline-step ${level >= 3 ? 'completed' : level === 2 ? 'active' : ''}`}>
          <div className="step-icon">
            <CheckCircle size={20} />
          </div>
          <div className="step-content">
            <h3>Visa Approuvé</h3>
            <p>Le dossier a été approuvé par les autorités compétentes.</p>
            {level === 2 && <p style={{ color: 'var(--warning)', marginTop: '8px', fontSize: '0.8rem' }}>En attente d'approbation finale.</p>}
          </div>
        </div>
      </div>

      {level === 3 && (
        <div className="mt-6" style={{ marginTop: '2rem', padding: '1.5rem', background: 'rgba(34, 197, 94, 0.1)', borderRadius: '12px', border: '1px solid rgba(34,197,94,0.2)', textAlign: 'center' }}>
          <h3 style={{ color: '#4ade80', marginBottom: '0.5rem' }}>Félicitations !</h3>
          <p>Votre demande a été approuvée. Vous pouvez vous présenter pour récupérer vos documents.</p>
        </div>
      )}
    </div>
  );
};

export default SuiviPage;
