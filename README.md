
<a id="readme-top"></a>

[![Contributors][contributors-shield]][contributors-url]
[![Forks][forks-shield]][forks-url]
[![Stargazers][stars-shield]][stars-url]
[![Issues][issues-shield]][issues-url]

<!-- PROJECT LOGO -->

<br />
<div align="center">
  <a href="https://github.com/thenightness/HausFix">
    <img src="backend/src/images/logo.png" alt="Logo" width="200" height="200">
  </a>
<h1 align="center">HausFix</h1>

  <p align="center">
    3FA081 Gruppe C
  </p>
</div>



<!-- INHALTSVERZEICHNIS -->
<details>
  <summary>Inhaltsverzeichnis</summary>
  <ol>
    <li>
      <a href="#ueber-das-Projekt">Über das Projekt</a>
      <ul>
        <li><a href="#built-with">Built With</a></li>
      </ul>
    </li>
    <li>
      <a href="#getting-started">Getting Started</a>
      <ul>
        <li><a href="#prerequisites">Prerequisites</a></li>
        <li><a href="#installation">Installation</a></li>
      </ul>
    </li>
    <li><a href="#usage">Usage</a></li>
    <li><a href="#roadmap">Roadmap</a></li>
    <li><a href="#contributing">Contributing</a></li>
    <li><a href="#license">License</a></li>
    <li><a href="#contact">Contact</a></li>
    <li><a href="#acknowledgments">Acknowledgments</a></li>
  </ol>
</details>



<!-- ÜBER DAS PROJEKT -->
## Über das Projekt

[![HausFix Screen Shot][hausfix-screenshot]](https://example.com)

Beschreibung folgt

<p align="right">(<a href="#readme-top">back to top</a>)</p>



### Built With
* [![Svelte][Svelte.dev]][Svelte-url]
* [![MariaDB][mariadb.org]][mariadb-url]
* [![Docker][Docker.com]][Docker-url]
* [![Java][Java.com]][Java-url]
* [![Maven][Maven.Apache.org]][Maven-url]

<p align="right">(<a href="#readme-top">back to top</a>)</p>



<!-- GETTING STARTED -->
## Getting Started

Beschreibung folgt.
Um eine lokale Kopie zum Laufen zu bringen, befolgen Sie den folgenden Schritten.

### Prerequisites

Zur Verwendung der Software werden folgende Programme benötigt
* Docker
  ```sh
  start /w "" "Docker Desktop Installer.exe" install
  ```
* Maven [Download ins Java Directory](https://maven.apache.org/download.cgi)
  ```sh
  
  ```

### Installation

1Clone the repo
   ```sh
   git clone https://github.com/thenightness/HausFix.git
   ```
3. Install NPM packages
   ```sh
   npm install
   ```
4. Enter your API in `config.js`
   ```js
   const API_KEY = 'ENTER YOUR API';
   ```
5. Change git remote url to avoid accidental pushes to base project
   ```sh
   git remote set-url origin thenightness/HausFix
   git remote -v # confirm the changes
   ```

<p align="right">(<a href="#readme-top">back to top</a>)</p>

<!-- USAGE EXAMPLES -->
## Dokumentation
### [Backend](backend/README.md)
### [Frontend](frontend/README.md)

<p align="right">(<a href="#readme-top">back to top</a>)</p>


### Top contributors:

<a href="https://github.com/thenightness/HausFix/graphs/contributors">
  <img src="https://contrib.rocks/image?repo=thenightness/HausFix" alt="contrib.rocks image" />
</a>

<p align="right">(<a href="#readme-top">back to top</a>)</p>



<!-- MARKDOWN LINKS & IMAGES -->
<!-- https://www.markdownguide.org/basic-syntax/#reference-style-links -->
[contributors-shield]: https://img.shields.io/github/contributors/thenightness/HausFix.svg?style=for-the-badge
[contributors-url]: https://github.com/thenightness/HausFix/graphs/contributors
[forks-shield]: https://img.shields.io/github/forks/thenightness/HausFix.svg?style=for-the-badge
[forks-url]: https://github.com/thenightness/HausFix/network/members
[stars-shield]: https://img.shields.io/github/stars/thenightness/HausFix.svg?style=for-the-badge
[stars-url]: https://github.com/thenightness/HausFix/stargazers
[issues-shield]: https://img.shields.io/github/issues/thenightness/HausFix.svg?style=for-the-badge
[issues-url]: https://github.com/thenightness/HausFix/issues
[hausfix-screenshot]: images/screenshot.png
[Svelte.dev]: https://img.shields.io/badge/Svelte-4A4A55?logo=svelte&logoColor=FF3E00
[Svelte-url]: https://svelte.dev/
[Mariadb.org]: https://img.shields.io/badge/MariaDB-C0765A?logo=mariadb&logoColor=white
[Mariadb-url]: https://mariadb.org/
[Docker.com]: https://img.shields.io/badge/Docker-328EEF?logo=docker&logoColor=white
[Docker-url]: https://www.docker.com/
[Java.com]: https://img.shields.io/badge/Java-ea2f30?logoColor=white
[Java-url]: https://www.java.com/de/
[Maven.Apache.org]: https://img.shields.io/badge/Maven-C7203E?logo=apachemaven&logoColor=white
[Maven-url]: https://maven.apache.org/