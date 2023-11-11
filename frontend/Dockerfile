FROM    node:latest

WORKDIR /app/front

COPY    package.json package-lock.json
COPY    . .

RUN     npm install
RUN     npm install -g @angular/cli

EXPOSE  4200

CMD     ["ng", "serve", "--host", "0.0.0.0"]
