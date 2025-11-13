# Despliegue en Oracle Cloud (OCI) - Backend Spring Boot

Guía rápida para construir la imagen, subirla a Oracle Container Registry (OCIR) y desplegarla en una instancia Compute o Container Instance.

## 1. Prerrequisitos
- Tenancy OCID y Namespace (lo ves en Identity > Tenancy Details).
- Usuario con API Key configurada o auth mediante contraseña para `docker login`.
- VCN + Subnet (preferible pública si expones el servicio directamente).
- Base de datos Postgres funcionando (Autonomous, VM propia, Container, etc.). Anota: host, puerto, base, usuario y contraseña.

## 2. Variables necesarias (recomendado usar Secrets / Vault)
```
DB_HOST=<host_postgres>
DB_PORT=5432
DB_NAME=goslintdb
SPRING_DATASOURCE_USERNAME=goslint
SPRING_DATASOURCE_PASSWORD=<password>
OPENROUTER_API_KEY=<opcional>
SPRING_PROFILES_ACTIVE=prod
SERVER_PORT=8080
```

## 3. Construir imagen local
```bash
docker build -t goslint-backend:1.0 .
```

## 4. Etiquetar para OCIR
Formato repo: `<region-code>.ocir.io/<namespace>/<repo>/<image>:<tag>`

Ejemplo (región Madrid = `eu-madrid-1`):
```bash
REGION=eu-madrid-1
NS=<tu_namespace>
REPO=goslint
docker tag goslint-backend:1.0 $REGION.ocir.io/$NS/$REPO/goslint-backend:1.0
```

## 5. Login a OCIR
```bash
docker login $REGION.ocir.io
Username: <tenancyNamespace>/<usuario>
Password: <contraseña o token>
```

## 6. Push
```bash
docker push $REGION.ocir.io/$NS/$REPO/goslint-backend:1.0
```

## 7. Despliegue en Container Instance (simplificado)
1. Crea Container Instance en OCI Console.
2. Agrega contenedor usando la imagen recién subida.
3. Define las variables de entorno (sección Environment Variables).
4. Expón puerto 8080 (Container Port 8080 -> Host Port 8080) o configura un LB.
5. Opcional: añade política de auto-restart y logs.

## 8. Despliegue en Compute (VM)
```bash
sudo yum install -y docker
sudo systemctl enable --now docker
docker login $REGION.ocir.io
docker pull $REGION.ocir.io/$NS/$REPO/goslint-backend:1.0
docker run -d --name goslint -p 8080:8080 \
  -e DB_HOST=<host_postgres> \
  -e SPRING_DATASOURCE_USERNAME=goslint \
  -e SPRING_DATASOURCE_PASSWORD=<password> \
  -e SPRING_PROFILES_ACTIVE=prod \
  $REGION.ocir.io/$NS/$REPO/goslint-backend:1.0
```

## 9. Health Check
Endpoint: `GET /actuator/health` (ya configurado). Úsalo para LB y monitoreo.

## 10. Logs
```bash
docker logs -f goslint
```

## 11. Actualizaciones
1. Construye nueva versión: `docker build -t goslint-backend:1.1 .`
2. Tag + push nueva versión.
3. En Container Instance: realiza redeploy seleccionando la nueva etiqueta.

## 12. Seguridad básica
- Asegura el Security List / NSG con solo puerto 8080 (HTTP/HTTPS) expuesto.
- Implementa HTTPS vía LB o Nginx reverse proxy.
- Usa Vault + OCI Secrets para contraseñas en lugar de variables planas si es producción.

## 13. Escalado
- Container Instances: crea más instancias y pon un Load Balancer delante.
- Opción avanzada: usar OKE (Kubernetes) y un Deployment + Service.

## 14. Errores comunes
| Problema | Solución |
|----------|---------|
| 401 al hacer push | Revisa namespace y formato de usuario `<namespace>/<user>` |
| La app no arranca (DB) | Verifica variables DB_HOST y credenciales |
| Healthcheck falla | Asegura que Actuator esté incluido y puerto expuesto |
| Timeout en OCIR | Verifica política de red / firewall / NSG |

## 15. Limpieza local
```bash
docker image prune -f
docker container prune -f
```

---
Listo. Ajusta versión/etiquetas según tu flujo (CI/CD) y considera firmar imágenes si requieres mayor seguridad.