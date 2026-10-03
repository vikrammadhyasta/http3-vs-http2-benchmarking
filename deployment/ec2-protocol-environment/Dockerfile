FROM nginx:latest

# Copy your app files
COPY trans_proto/ /var/www/html/

# Copy both nginx conf files into conf.d (NGINX auto-loads them)
COPY trans_proto/server2.conf /etc/nginx/conf.d/server2.conf

EXPOSE 80 3000

CMD ["nginx", "-g", "daemon off;"]
