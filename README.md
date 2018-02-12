# udacity-capstone

1. Criei um módulo *appadmin* apenas para "testes". Este módulo é responsável por criar o cardápio no firebase. Ele não faz parte do projeto.
2. O QR Code é validado por uma regra no banco do Firebase. Para facilitar os testes estou mudando esta validação para aceitar qualquer tipo de código numérico (exemplo: "123456" é válido, "asdfgh" é inválido.)
  * QR Code válido (Obs.: só é possível adicionar 1 ponto por dia. O ponto está associado ao dia.):
  
  ![QR Code válido](https://chart.googleapis.com/chart?cht=qr&chl=123456&chs=180x180&choe=UTF-8&chld=L|2)
  * QR Code inválido:
  
  ![QR Code inválido](https://chart.googleapis.com/chart?cht=qr&chl=asdfgh&chs=180x180&choe=UTF-8&chld=L|2)

3. O cardápio, os pedidos e o QR Code são apenas para o dia atual. Criei um usuário de teste com alguns pedidos. email: "teste01@teste.com", senha: "123456".
4. Há um popup para alertar se é feriado. Ele funciona apenas em dia de feriado. (Dia 12 é carnaval! 😃 )
