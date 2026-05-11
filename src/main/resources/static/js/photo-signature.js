// ======================================================
// WEBCAM – capture photo d'identité
// ======================================================
let webcamStream = null;
let capturedPhotoDataUrl = null;

async function startWebcam() {
    try {
        const video = document.getElementById('webcamVideo');
        webcamStream = await navigator.mediaDevices.getUserMedia({ video: { width: 640, height: 480 }, audio: false });
        video.srcObject = webcamStream;
        video.style.display = 'block';
        document.getElementById('btnStartCam').style.display  = 'none';
        document.getElementById('btnCapture').style.display   = 'inline-flex';
    } catch (err) {
        alert('Impossible d\'accéder à la webcam : ' + err.message);
    }
}

function capturePhoto() {
    const video  = document.getElementById('webcamVideo');
    const canvas = document.getElementById('webcamCanvas');
    canvas.width  = video.videoWidth  || 640;
    canvas.height = video.videoHeight || 480;
    canvas.getContext('2d').drawImage(video, 0, 0, canvas.width, canvas.height);
    capturedPhotoDataUrl = canvas.toDataURL('image/jpeg', 0.88);
    if (webcamStream) webcamStream.getTracks().forEach(function(t){ t.stop(); });
    video.style.display = 'none';
    canvas.style.display = 'block';
    document.getElementById('btnCapture').style.display   = 'none';
    document.getElementById('btnSavePhoto').style.display = 'inline-flex';
}

async function savePhoto() {
    if (!capturedPhotoDataUrl) return;
    const demandeId = document.getElementById('demandeFormEdit') ?
        window.location.pathname.split('/')[2] : null;
    const id = window._demandeId || demandeId;
    if (!id) return alert('Identifiant de demande non trouvé.');
    const res = await fetch('/demandes/' + id + '/photo-identite', {
        method: 'POST',
        headers: { 'Content-Type': 'text/plain' },
        body: capturedPhotoDataUrl
    });
    if (res.ok) {
        let prev = document.getElementById('savedPhotoPreview');
        if (!prev) {
            prev = document.createElement('img');
            prev.id = 'savedPhotoPreview';
            prev.style.cssText = 'width:100%;height:100%;object-fit:cover;';
            const lbl = document.getElementById('noPhotoLabel');
            if (lbl) lbl.replaceWith(prev);
        }
        prev.src = capturedPhotoDataUrl;
        document.getElementById('photoStatus').style.display = 'block';
        document.getElementById('btnSavePhoto').style.display = 'none';
    } else {
        alert('Erreur lors de la sauvegarde de la photo.');
    }
}

// ======================================================
// SIGNATURE PAD
// ======================================================
(function initSignaturePad() {
    var canvas = document.getElementById('signaturePad');
    if (!canvas) return;
    var ctx = canvas.getContext('2d');
    ctx.strokeStyle = '#1e3a8a';
    ctx.lineWidth   = 2.5;
    ctx.lineCap     = 'round';
    ctx.lineJoin    = 'round';
    var drawing = false, lastX = 0, lastY = 0;

    function getXY(e) {
        var rect = canvas.getBoundingClientRect();
        var sX   = canvas.width  / rect.width;
        var sY   = canvas.height / rect.height;
        var src  = e.touches ? e.touches[0] : e;
        return [(src.clientX - rect.left) * sX, (src.clientY - rect.top) * sY];
    }

    canvas.addEventListener('mousedown',  function(e){ drawing = true; var p=getXY(e); lastX=p[0]; lastY=p[1]; });
    canvas.addEventListener('touchstart', function(e){ e.preventDefault(); drawing = true; var p=getXY(e); lastX=p[0]; lastY=p[1]; }, { passive: false });
    canvas.addEventListener('mouseup',    function(){ drawing = false; });
    canvas.addEventListener('mouseleave', function(){ drawing = false; });
    canvas.addEventListener('touchend',   function(){ drawing = false; });

    function draw(e) {
        if (!drawing) return;
        if (e.touches) e.preventDefault();
        var p = getXY(e);
        ctx.beginPath(); ctx.moveTo(lastX, lastY); ctx.lineTo(p[0], p[1]); ctx.stroke();
        lastX = p[0]; lastY = p[1];
    }
    canvas.addEventListener('mousemove', draw);
    canvas.addEventListener('touchmove', draw, { passive: false });
})();

function clearSignature() {
    var c = document.getElementById('signaturePad');
    c.getContext('2d').clearRect(0, 0, c.width, c.height);
    document.getElementById('sigStatus').style.display = 'none';
}

async function saveSignature() {
    var canvas  = document.getElementById('signaturePad');
    var dataUrl = canvas.toDataURL('image/png');
    var id = window._demandeId || window.location.pathname.split('/')[2];
    if (!id) return alert('Identifiant de demande non trouvé.');
    var res = await fetch('/demandes/' + id + '/signature', {
        method: 'POST',
        headers: { 'Content-Type': 'text/plain' },
        body: dataUrl
    });
    if (res.ok) {
        var prev = document.getElementById('savedSignaturePreview');
        if (prev) prev.src = dataUrl;
        document.getElementById('sigStatus').style.display = 'block';
    } else {
        alert('Erreur lors de la sauvegarde de la signature.');
    }
}
