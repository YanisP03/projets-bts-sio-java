# Projet Java — Gestion des activités et notifications

> Projet final réalisé par Yanis Perrin · [GitHub](https://github.com/YanisP03) · [LinkedIn](https://www.linkedin.com/in/yanis-perrin-a63316357/)

**Stack :** Java 17 · MySQL · Swing · JDBC

---

## Présentation

Application de bureau Java permettant de gérer des activités, des calendriers et des notifications. Elle couvre l'ensemble du cycle : création d'activités, planification de créneaux, inscriptions des participants, évaluations et envoi de rappels automatiques.

L'interface graphique est développée en Swing et la persistance est assurée via MySQL avec une architecture DAO.

---

## Fonctionnalités

- **Activités** — ajout, modification et suppression
- **Calendrier** — planification de créneaux horaires
- **Inscriptions** — gestion des participants et des enfants
- **Notifications** — rappels automatiques liés aux activités
- **Évaluations** — notation des activités par les utilisateurs
- **Espace admin / responsable** — interfaces dédiées par rôle

---

## Prérequis

- [Java JDK 17+](https://www.oracle.com/java/technologies/javase-jdk17-downloads.html)
- [Laragon](https://laragon.org/) (Windows) ou [MySQL Workbench](https://www.mysql.com/products/workbench/) (macOS)
- Un IDE : [Eclipse](https://www.eclipse.org/) ou [IntelliJ IDEA](https://www.jetbrains.com/idea/)

---

## Installation

### 1. Base de données

Démarrez Laragon et vérifiez que le serveur MySQL est actif, puis importez le fichier SQL fourni dans le dossier `ProjetJAVADossier` :

```bash
mysql -u root -p -e "CREATE DATABASE camp_activites2;"
mysql -u root -p camp_activites2 < camp_activites2.sql
```

Vous pouvez aussi utiliser HeidiSQL ou phpMyAdmin pour importer le fichier graphiquement.

### 2. Configuration de la connexion

Dans `src/bdd/DatabaseConnection.java`, vérifiez que les paramètres correspondent à votre environnement :

```java
String url  = "jdbc:mysql://localhost:3306/camp_activites2";
String user = "root";
String password = "";
```

### 3. Ajout des dépendances (Eclipse)

Les fichiers `.jar` sont inclus dans le dossier `ProjetJAVADossier` :

1. Clic droit sur le projet → **Build Path > Configure Build Path**
2. Onglet **Libraries** → sélectionnez **Modulepath**
3. Cliquez sur **Add External JARs** et ajoutez :
   - `mysql-connector-j-9.1.0.jar`
   - `jbcrypt-0.4.jar`
4. Validez avec **Apply and Close**

### 4. Lancement

Compilez le projet dans votre IDE puis exécutez `src/main/Main.java`. Une fenêtre de connexion s'ouvrira.

---

## Comptes de test

| Rôle | Email | Mot de passe |
|------|-------|--------------|
| Admin | admin@gmail.com | wN1'y-naMkDQNS!1 |
| Responsable | responsable@gmail.com | G9!vR2m#Tq4b |

> ⚠️ Ces identifiants sont réservés aux tests. Ne les utilisez jamais en production.

---

## Structure du projet

```
ProjetJava-Final/
└── src/
    ├── bdd/
    │   └── DatabaseConnection.java       # Connexion MySQL
    ├── dao/
    │   ├── ActiviteDAO.java
    │   ├── CalendrierDAO.java
    │   ├── InscriptionActiviteDAO.java
    │   ├── NotificationDAO.java
    │   └── UtilisateurDAO.java
    ├── model/
    │   ├── Activite.java
    │   ├── Calendrier.java
    │   ├── Evaluation.java
    │   ├── InscriptionActivite.java
    │   ├── Notification.java
    │   ├── NotificationService.java
    │   └── Utilisateur.java
    ├── view/
    │   ├── FenetreAdmin.java
    │   ├── FenetreCalendrier.java
    │   ├── FenetreConnexion.java
    │   ├── FenetreGestionActivites.java
    │   ├── FenetreInscription.java
    │   ├── FenetreInscriptionsActivite.java
    │   ├── FenetreNotification.java
    │   ├── FenetreNotificationGlobales.java
    │   ├── FenetrePrincipale.java
    │   ├── FenetreResponsable.java
    │   └── FenetreTicketsIncidents.java
    └── main/
        └── Main.java
```

---

## Architecture

Le projet suit une architecture en couches :

- **`bdd`** — connexion à la base de données via JDBC
- **`model`** — classes métier (entités et services)
- **`dao`** — accès aux données, une classe par entité
- **`view`** — interfaces graphiques Swing par rôle et fonctionnalité
- **`main`** — point d'entrée de l'application

---

## Dépannage

**Impossible de se connecter à la base de données** — vérifiez que le serveur MySQL est bien démarré dans Laragon et que les identifiants dans `DatabaseConnection.java` sont corrects.

**Erreur à l'import du fichier SQL** — vérifiez que la base `camp_activites2` n'existe pas déjà, ou supprimez-la avant de réimporter. En dernier recours, copiez le fichier SQL directement dans le dossier de Laragon.

**Librairies non reconnues** — assurez-vous que les `.jar` sont bien ajoutés au `Modulepath` et non au `Classpath` dans la configuration Eclipse.

---

## Licence

Projet libre — modification et redistribution autorisées.
