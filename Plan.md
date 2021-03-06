# LineBall

## Разработчики
[Максим Елькин](https://github.com/maximelkin) (M3239), [Кирилл Карбушев](https://github.com/KiriosK) (M3238), [Глеб Рябчиков](https://github.com/glebvr) (M3238)

## Краткое описание
Игра ведётся через интернет. Участвуют два игрока, у каждого есть шарик красного или синего цвета и возможность рисовать на игровом поле *стены* того же цвета, о которые ударяются шарики, ускоряясь или замедляясь. При столкновении шариков или вылете одного из них за пределы поля игра заканчивается. 

В игре есть система рейтинга, который хранится на сервере, через который также ведётся поиск игроков.

## Функционал
### Основной
* 2 противника, у каждого шарик
* Каждый может рисовать стены (отрезки)
* Удар шарика о стену того же цвета замедляет шарик
* Удар шарика о стену другого цвета ускоряет шарик
* Стена ломается: для своего цвета - одним ударом шарика, для другого цвета - двумя ударами
* Удар шариков с равными скоростями - отскок

### Окончание игры
* Столкновение шариков (побеждает более быстрый)
* Вылет одного из шариков за пределы игрового поля
* Остановка шарика
* Лив противника

## Графический концепт
<img src="https://pp.vk.me/c636027/v636027527/286ab/h4VzwyviFUo.jpg" width="300">
