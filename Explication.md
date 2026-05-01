Atao saisie ny champ obligatoire de cochena ny piece justificative obligatoire izay vao afaka insérena

-Afaka modifiena ny information saisie(mety hoe diso ny numéro de passport na contact na tonga nitondra an'ilay pièce justificative optionnel) .

 Tsy miova ny état any fa mbola dossier crée foana fa ny information any no niova .

 

 Refa vita ny saisie (afa-po ilay demandeur = imprimena ilay information ao anaty base de donnée[attestation recépicé dossier(sprint 2 na 3 -> mamoaka pdf(averina ao daholo ny information etat civil , info passport , momban'ny visa , momban'ny liste des physique hoe ireto ny dossier voaray) raha misy diso de miverina amleh teo fa ny état mantsy mbola tsy niova fa crée fotsiny)])

 Refa afa po ilay demandeur => manao scan an'ily dossier physique(pièce justificative rehetra) (upload dossier physique (sary)) de attachena amin'ily demande

 

 Rehefa vita daholo ilay scan an'ilay dossier physique de miteny ilay opérateur de saisie hoe tsy dossier créé intsony fa dossier scanné na hoe scan terminé.

 R.Q : Rehefa SCAN TERMINE ilay izy de TSY AZO MODIFIENA intsony ilay information tafiditra , fa rehefa CREE izy de mbola afaka modifiena foana na dia manova aza izy hoe izaho tsy investisseur indray oatra fa travailleur.

 

 Possible sary maromaro par pièce justificative no azo . Izany hoe mifandray amin'ilay pièces justiciatif sy ilay demande ilay sary mivoaka avy any. (sprint 2 na 3)

 => Misy boutton miteny hoe tapitra ny scan après upload fichier .

 

 resaka SANS DONNÉES ANTERIEUR

 Contexte :

 -NORMAL : Olona manao nouvelle demande (mampiditra info anaty appli) -> mandalo an'ilay étape retraretra -> mahazo visa mipetraka @ passport sy carte résident -> Any aoriana any izy very ilay carte résident(mbola tsy tapitra izany ilay visa , mbola tsy lany ilay visa) , de mangataka izy hoe mba hanao duplacata aho (mampiditra numéro fotsiny izy ao anatin'ilay système amin'izay fotoana izay de azontsika daholo ny information rehetra teo aloha satria efa natao saisie tao anatin'ilay système ilay izy) na hanao transfert de visa (niova passport na very passport => mampiditra fotsiny an'ilay numéro de visa na numéro titre).

 

 OLANA : tsy nanana donnée isika ho an'ilay teo aloha izany hoe tsy no-saisissena tao amintsika ilay information any fa mampiseho preuve izy hoe izaho anie nanana carte résident valable jusqu'à 2030 fa very ilay izy fa ity misy photocopie => tsy maintsy traitena ilay izy , miverina manao nouveau titre demande (amin'izay fotoana izay ilay demande tonga de validé , otra hoe efa namoaka visa sy titre de séjour) => en parallèle amin'io manao nouvelle demande duplicata na  transfert de visa.

 -> manao demande nouveau titre ohatran'ilay taloha de aveo manao demande mifandray amin'iny hoe duplicata sa transfert de visa

 

 STATUT :

 Si normal : CRÉÉ -> SCAN TERMINÉ -> VISA APPROUVÉ(misy titre de séjour any = CARTE RÉSIDENT sy VISA)

 SI sans donnée antérieur : Tonga de VISA APPROUVÉ -> tonga de rattaché amin'iny ilay demande duplicata na demande transfert.

 

 Sprint 1 : resaka saisie donnée normal ilay misy resaka obligatoire

 Sprint 2 : ilay misy sans donnée antérieur

 Sprint 3 : scan terminé (tsy apoitra ny boutton tant que mbola misy pièce justificative na optionnel aza tsy fenony)

 

 CONCEPTION :

DEMANDEUR: nom obli , prénom non obli , date naissance OBLI , situation familiale (table mitokana de référence an'iny) , adresse mada OBLI , nationalité OBLI (tsy maintsy table mitokana foreing key) , created date , updated date , info état civil.

VISA TRANSFORMABLE : misy id_demandeur (ze farany ao fona ny information visa trasformable actuel an’ilay demandeur) , id passport .

DEMANDE : id demandeur , type demande(nouveau titre , duplicata sns) , id type de visa(travalleur , investisseur )

RQ : passport tsy mety raha tonga de apetraka ao @ demandeur satria possible very iny passport iny.

VISA : id demande , id passeport  , date debut , date fin

DEMANDE STATUT (historique)

CARTE RESIDENT : id demande , id passport , date debut , date fin

numéro obli , mail facultatif .

Demandeur peut avoir plusieurs passport : one to many

Ao anaty passport no misy demandeur .


Raha manampy passport vaovao ilay demandeur de miampy ligne vaovao ao anaty table passport associé aminy . Ao anaty passport no misy id demandeur .


de id passport no associena @ visa sy carte résident fa tsy id demandeur izany