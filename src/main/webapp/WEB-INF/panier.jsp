<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Mon Panier</title>
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

        .empty-cart {
            background: white;
            padding: 60px 20px;
            text-align: center;
            border-radius: 8px;
            box-shadow: 0 2px 4px rgba(0,0,0,0.1);
        }

        .empty-cart-icon {
            font-size: 80px;
            color: #bdc3c7;
            margin-bottom: 20px;
        }

        .empty-cart h2 {
            color: #2c3e50;
            margin-bottom: 10px;
        }

        .empty-cart p {
            color: #7f8c8d;
            margin-bottom: 30px;
        }

        .cart-content {
            display: grid;
            grid-template-columns: 1fr 350px;
            gap: 20px;
        }

        .cart-items {
            background: white;
            border-radius: 8px;
            box-shadow: 0 2px 4px rgba(0,0,0,0.1);
            overflow: hidden;
        }

        .cart-item {
            padding: 20px;
            border-bottom: 1px solid #ecf0f1;
            display: grid;
            grid-template-columns: 100px 1fr auto;
            gap: 20px;
            align-items: center;
        }

        .cart-item:last-child {
            border-bottom: none;
        }

        .item-image {
            width: 100px;
            height: 100px;
            object-fit: cover;
            border-radius: 8px;
            background: #ecf0f1;
        }

        .item-details {
            flex: 1;
        }

        .item-name {
            font-size: 18px;
            font-weight: 600;
            color: #2c3e50;
            margin-bottom: 8px;
        }

        .item-price {
            color: #27ae60;
            font-size: 16px;
            font-weight: 600;
            margin-bottom: 12px;
        }

        .quantity-controls {
            display: flex;
            align-items: center;
            gap: 10px;
            margin-bottom: 12px;
        }

        .quantity-btn {
            width: 32px;
            height: 32px;
            border: 1px solid #bdc3c7;
            background: white;
            border-radius: 4px;
            cursor: pointer;
            font-size: 18px;
            display: flex;
            align-items: center;
            justify-content: center;
            transition: all 0.3s;
        }

        .quantity-btn:hover {
            background: #ecf0f1;
            border-color: #95a5a6;
        }

        .quantity-input {
            width: 60px;
            height: 32px;
            text-align: center;
            border: 1px solid #bdc3c7;
            border-radius: 4px;
            font-size: 16px;
        }

        .item-subtotal {
            font-size: 18px;
            font-weight: 700;
            color: #2c3e50;
            margin-bottom: 8px;
        }

        .item-actions {
            display: flex;
            gap: 10px;
            flex-direction: column;
            align-items: flex-end;
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

        .btn-delete {
            background: #e74c3c;
            color: white;
        }

        .btn-delete:hover {
            background: #c0392b;
            transform: translateY(-2px);
            box-shadow: 0 4px 8px rgba(231, 76, 60, 0.3);
        }

        .btn-primary {
            background: #3498db;
            color: white;
            width: 100%;
        }

        .btn-primary:hover {
            background: #2980b9;
            transform: translateY(-2px);
            box-shadow: 0 4px 8px rgba(52, 152, 219, 0.3);
        }

        .btn-success {
            background: #27ae60;
            color: white;
            width: 100%;
            padding: 15px;
            font-size: 16px;
        }

        .btn-success:hover {
            background: #229954;
            transform: translateY(-2px);
            box-shadow: 0 4px 8px rgba(39, 174, 96, 0.3);
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

        .cart-summary {
            background: white;
            padding: 20px;
            border-radius: 8px;
            box-shadow: 0 2px 4px rgba(0,0,0,0.1);
            height: fit-content;
            position: sticky;
            top: 20px;
        }

        .summary-title {
            font-size: 20px;
            font-weight: 600;
            color: #2c3e50;
            margin-bottom: 20px;
            padding-bottom: 15px;
            border-bottom: 2px solid #ecf0f1;
        }

        .summary-row {
            display: flex;
            justify-content: space-between;
            margin-bottom: 15px;
            font-size: 15px;
        }

        .summary-row.total {
            font-size: 20px;
            font-weight: 700;
            color: #27ae60;
            padding-top: 15px;
            border-top: 2px solid #ecf0f1;
            margin-top: 15px;
        }

        .summary-actions {
            margin-top: 25px;
            display: flex;
            flex-direction: column;
            gap: 10px;
        }

        @media (max-width: 968px) {
            .cart-content {
                grid-template-columns: 1fr;
            }

            .cart-item {
                grid-template-columns: 80px 1fr;
                gap: 15px;
            }

            .item-actions {
                grid-column: 1 / -1;
                flex-direction: row;
                justify-content: space-between;
                width: 100%;
            }
        }

        @media (max-width: 576px) {
            .container {
                padding: 10px;
            }

            .cart-item {
                grid-template-columns: 1fr;
                text-align: center;
            }

            .item-image {
                width: 100%;
                height: 200px;
            }

            .quantity-controls {
                justify-content: center;
            }

            .item-actions {
                flex-direction: column;
            }
        }
    </style>
</head>
<body>
<div class="container">
    <div class="header">
        <h1>🛒 Mon Panier</h1>
        <div class="breadcrumb">
            <a href="${pageContext.request.contextPath}/index">Accueil</a> / Panier
        </div>
    </div>

    <c:choose>
        <c:when test="${empty panier or empty panier.lignesPanier}">
            <div class="empty-cart">
                <div class="empty-cart-icon">🛒</div>
                <h2>Votre panier est vide</h2>
                <p>Vous n'avez pas encore ajouté de produits à votre panier</p>
                <a href="${pageContext.request.contextPath}/index" class="btn btn-primary">
                    Continuer mes achats
                </a>
            </div>
        </c:when>
        <c:otherwise>
            <div class="cart-content">
                <div class="cart-items">
                    <c:forEach var="ligne" items="${panier.lignesPanier}">
                        <div class="cart-item">
                            <img src="${pageContext.request.contextPath}/images/${ligne.produit.image}"
                                 alt="${ligne.produit.nom}"
                                 class="item-image"
                                 onerror="this.src='${pageContext.request.contextPath}/images/no-image.png'">

                            <div class="item-details">
                                <div class="item-name">${ligne.produit.nom}</div>
                                <div class="item-price">
                                    <fmt:formatNumber value="${ligne.prixUnitaire}" type="currency" currencySymbol="DH" /> / unité
                                </div>
                                <div class="quantity-controls">
                                    <form action="${pageContext.request.contextPath}/panier" method="post" style="display: inline;">
                                        <input type="hidden" name="action" value="update_quantity">
                                        <input type="hidden" name="id_ligne" value="${ligne.id}">
                                        <input type="hidden" name="quantite" value="${ligne.quantite - 1}">
                                        <button type="submit" class="quantity-btn" ${ligne.quantite <= 1 ? 'disabled' : ''}>−</button>
                                    </form>

                                    <input type="number"
                                           class="quantity-input"
                                           value="${ligne.quantite}"
                                           min="1"
                                           readonly>

                                    <form action="${pageContext.request.contextPath}/panier" method="post" style="display: inline;">
                                        <input type="hidden" name="action" value="update_quantity">
                                        <input type="hidden" name="id_ligne" value="${ligne.id}">
                                        <input type="hidden" name="quantite" value="${ligne.quantite + 1}">
                                        <button type="submit" class="quantity-btn">+</button>
                                    </form>
                                </div>
                            </div>

                            <div class="item-actions">
                                <div class="item-subtotal">
                                    <fmt:formatNumber value="${ligne.sousTotal}" type="currency" currencySymbol="DH" />
                                </div>
                                <form action="${pageContext.request.contextPath}/panier" method="post" onsubmit="return confirm('Êtes-vous sûr de vouloir supprimer cet article ?');">
                                    <input type="hidden" name="action" value="delete">
                                    <input type="hidden" name="id_ligne" value="${ligne.id}">
                                    <button type="submit" class="btn btn-delete">🗑️ Supprimer</button>
                                </form>
                            </div>
                        </div>
                    </c:forEach>
                </div>

                <div class="cart-summary">
                    <h2 class="summary-title">Résumé de la commande</h2>

                    <div class="summary-row">
                        <span>Nombre d'articles:</span>
                        <strong>${panier.lignesPanier.size()}</strong>
                    </div>

                    <div class="summary-row">
                        <span>Sous-total:</span>
                        <strong><fmt:formatNumber value="${panier.montantTotal}" type="currency" currencySymbol="DH" /></strong>
                    </div>

                    <div class="summary-row">
                        <span>Livraison:</span>
                        <strong>Gratuite</strong>
                    </div>

                    <div class="summary-row total">
                        <span>Total:</span>
                        <span><fmt:formatNumber value="${panier.montantTotal}" type="currency" currencySymbol="DH" /></span>
                    </div>

                    <div class="summary-actions">
                        <form action="${pageContext.request.contextPath}/commande" method="post">
                            <input type="hidden" name="action" value="passer_commande">
                            <input type="hidden" name="id_panier" value="${panier.id}">
                            <button type="submit" class="btn btn-success">
                                ✓ Passer la commande
                            </button>
                        </form>

                        <a href="${pageContext.request.contextPath}/index" class="btn btn-secondary">
                            ← Continuer mes achats
                        </a>
                    </div>
                </div>
            </div>
        </c:otherwise>
    </c:choose>
</div>
</body>
</html>