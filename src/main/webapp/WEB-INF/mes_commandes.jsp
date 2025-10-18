<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Mes Commandes</title>
    <style>
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }

        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            background: #f5f5f5;
            color: #333;
            line-height: 1.6;
        }

        .container {
            max-width: 1200px;
            margin: 0 auto;
            padding: 20px;
        }

        .header {
            background: white;
            padding: 20px;
            margin-bottom: 30px;
            border-radius: 8px;
            box-shadow: 0 2px 4px rgba(0,0,0,0.1);
        }

        .header h1 {
            color: #2c3e50;
            font-size: 28px;
            margin-bottom: 10px;
        }

        .breadcrumb {
            color: #7f8c8d;
            font-size: 14px;
        }

        .breadcrumb a {
            color: #3498db;
            text-decoration: none;
        }

        .breadcrumb a:hover {
            text-decoration: underline;
        }

        .success-message {
            background: #d4edda;
            color: #155724;
            padding: 15px 20px;
            border-radius: 8px;
            margin-bottom: 20px;
            border-left: 4px solid #28a745;
            display: flex;
            align-items: center;
            gap: 10px;
        }

        .success-message::before {
            content: "✓";
            font-size: 24px;
            font-weight: bold;
        }

        .empty-state {
            background: white;
            padding: 60px 20px;
            text-align: center;
            border-radius: 8px;
            box-shadow: 0 2px 4px rgba(0,0,0,0.1);
        }

        .empty-state-icon {
            font-size: 80px;
            color: #bdc3c7;
            margin-bottom: 20px;
        }

        .empty-state h2 {
            color: #2c3e50;
            margin-bottom: 10px;
        }

        .empty-state p {
            color: #7f8c8d;
            margin-bottom: 30px;
        }

        .commandes-grid {
            display: grid;
            gap: 20px;
        }

        .commande-card {
            background: white;
            border-radius: 8px;
            box-shadow: 0 2px 4px rgba(0,0,0,0.1);
            overflow: hidden;
            transition: transform 0.3s, box-shadow 0.3s;
        }

        .commande-card:hover {
            transform: translateY(-2px);
            box-shadow: 0 4px 12px rgba(0,0,0,0.15);
        }

        .commande-header {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            padding: 20px;
            display: grid;
            grid-template-columns: 1fr auto;
            gap: 20px;
            align-items: center;
        }

        .commande-numero {
            font-size: 18px;
            font-weight: 600;
        }

        .commande-date {
            font-size: 14px;
            opacity: 0.9;
        }

        .commande-statut {
            padding: 8px 16px;
            border-radius: 20px;
            font-size: 14px;
            font-weight: 600;
            text-align: center;
        }

        .statut-en-attente {
            background: #fff3cd;
            color: #856404;
        }

        .statut-confirmee {
            background: #d1ecf1;
            color: #0c5460;
        }

        .statut-expediee {
            background: #d4edda;
            color: #155724;
        }

        .statut-livree {
            background: #28a745;
            color: white;
        }

        .statut-annulee {
            background: #f8d7da;
            color: #721c24;
        }

        .commande-body {
            padding: 20px;
        }

        .commande-info {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
            gap: 15px;
            margin-bottom: 20px;
        }

        .info-item {
            display: flex;
            flex-direction: column;
            gap: 5px;
        }

        .info-label {
            color: #7f8c8d;
            font-size: 14px;
        }

        .info-value {
            color: #2c3e50;
            font-weight: 600;
            font-size: 16px;
        }

        .commande-produits {
            border-top: 1px solid #ecf0f1;
            padding-top: 15px;
        }

        .produits-title {
            font-weight: 600;
            color: #2c3e50;
            margin-bottom: 10px;
            font-size: 16px;
        }

        .produit-item {
            display: flex;
            justify-content: space-between;
            align-items: center;
            padding: 10px;
            background: #f8f9fa;
            border-radius: 6px;
            margin-bottom: 8px;
        }

        .produit-item:last-child {
            margin-bottom: 0;
        }

        .produit-info {
            flex: 1;
        }

        .produit-nom {
            font-weight: 600;
            color: #2c3e50;
            margin-bottom: 4px;
        }

        .produit-details {
            font-size: 14px;
            color: #7f8c8d;
        }

        .produit-prix {
            font-weight: 700;
            color: #27ae60;
            font-size: 16px;
        }

        .commande-actions {
            margin-top: 15px;
            display: flex;
            gap: 10px;
            justify-content: flex-end;
        }

        .btn {
            padding: 10px 20px;
            border: none;
            border-radius: 6px;
            cursor: pointer;
            font-size: 14px;
            font-weight: 600;
            transition: all 0.3s;
            text-decoration: none;
            display: inline-block;
            text-align: center;
        }

        .btn-primary {
            background: #3498db;
            color: white;
        }

        .btn-primary:hover {
            background: #2980b9;
            transform: translateY(-2px);
            box-shadow: 0 4px 8px rgba(52, 152, 219, 0.3);
        }

        .btn-secondary {
            background: white;
            color: #3498db;
            border: 2px solid #3498db;
        }

        .btn-secondary:hover {
            background: #3498db;
            color: white;
        }

        .btn-danger {
            background: #e74c3c;
            color: white;
        }

        .btn-danger:hover {
            background: #c0392b;
        }

        @media (max-width: 768px) {
            .commande-header {
                grid-template-columns: 1fr;
            }

            .commande-info {
                grid-template-columns: 1fr;
            }

            .commande-actions {
                flex-direction: column;
            }

            .btn {
                width: 100%;
            }
        }
    </style>
</head>
<body>
<div class="container">
    <div class="header">
        <h1>📦 Mes Commandes</h1>
        <div class="breadcrumb">
            <a href="${pageContext.request.contextPath}/index">Accueil</a> / Mes Commandes
        </div>
    </div>

    <!-- Message de succès -->
    <c:if test="${not empty sessionScope.successMessage}">
        <div class="success-message">
                ${sessionScope.successMessage}
        </div>
        <c:remove var="successMessage" scope="session" />
    </c:if>

    <!-- Liste des commandes -->
    <c:choose>
        <c:when test="${empty commandes}">
            <div class="empty-state">
                <div class="empty-state-icon">📦</div>
                <h2>Aucune commande pour le moment</h2>
                <p>Vous n'avez pas encore passé de commande</p>
                <a href="${pageContext.request.contextPath}/index" class="btn btn-primary">
                    Découvrir nos produits
                </a>
            </div>
        </c:when>
        <c:otherwise>
            <div class="commandes-grid">
                <c:forEach var="commande" items="${commandes}">
                    <div class="commande-card">
                        <div class="commande-header">
                            <div>
                                <div class="commande-numero">
                                    Commande #${commande.numeroCommande}
                                </div>
                                <div class="commande-date">
                                    <fmt:formatDate value="${commande.dateCommande}" pattern="dd MMMM yyyy 'à' HH:mm" />
                                </div>
                            </div>
                            <div class="commande-statut statut-${commande.statut.toLowerCase().replace('_', '-')}">
                                <c:choose>
                                    <c:when test="${commande.statut == 'EN_ATTENTE'}">En attente</c:when>
                                    <c:when test="${commande.statut == 'CONFIRMEE'}">Confirmée</c:when>
                                    <c:when test="${commande.statut == 'EXPEDIEE'}">Expédiée</c:when>
                                    <c:when test="${commande.statut == 'LIVREE'}">Livrée</c:when>
                                    <c:when test="${commande.statut == 'ANNULEE'}">Annulée</c:when>
                                    <c:otherwise>${commande.statut}</c:otherwise>
                                </c:choose>
                            </div>
                        </div>

                        <div class="commande-body">
                            <div class="commande-info">
                                <div class="info-item">
                                    <span class="info-label">💰 Montant total</span>
                                    <span class="info-value">
                                            <fmt:formatNumber value="${commande.montantTotal}" type="currency" currencySymbol="DH" />
                                        </span>
                                </div>
                                <div class="info-item">
                                    <span class="info-label">📍 Adresse de livraison</span>
                                    <span class="info-value">${commande.adresseLivraison}</span>
                                </div>
                                <div class="info-item">
                                    <span class="info-label">📦 Nombre d'articles</span>
                                    <span class="info-value">${commande.lignesCommande.size()} article(s)</span>
                                </div>
                            </div>

                            <div class="commande-produits">
                                <div class="produits-title">Produits commandés</div>
                                <c:forEach var="ligne" items="${commande.lignesCommande}">
                                    <div class="produit-item">
                                        <div class="produit-info">
                                            <div class="produit-nom">${ligne.produit.nom}</div>
                                            <div class="produit-details">
                                                Quantité: ${ligne.quantite} ×
                                                <fmt:formatNumber value="${ligne.prixUnitaire}" type="currency" currencySymbol="DH" />
                                            </div>
                                        </div>
                                        <div class="produit-prix">
                                            <fmt:formatNumber value="${ligne.sousTotal}" type="currency" currencySymbol="DH" />
                                        </div>
                                    </div>
                                </c:forEach>
                            </div>

                            <div class="commande-actions">
                                <a href="${pageContext.request.contextPath}/commande?action=details&numero=${commande.numeroCommande}"
                                   class="btn btn-primary">
                                    📄 Voir détails
                                </a>
                            </div>
                        </div>
                    </div>
                </c:forEach>
            </div>
        </c:otherwise>
    </c:choose>

    <div style="margin-top: 30px; text-align: center;">
        <a href="${pageContext.request.contextPath}/index" class="btn btn-secondary">
            ← Retour à l'accueil
        </a>
    </div>
</div>
</body>
</html>