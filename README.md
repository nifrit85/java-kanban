# java-kanban
## Интерфейс пользователя
1. Пользователь может создавать только __Обычные задачи__.
2. Пользователь может перетаскивать карточки.

### Правила перетаскивая задач
1. Если перетащить простую задачу на простую, первая станет подзадачей для второй.Вторая зачада превратиться в Эпик
2. Если перетащить простую задачу на Эпик, она станет подзачей.
3. Если перетащить простую задачу на подзадачу, то поздазача станет Эпиком и покинет свой Эпик, а простая, его подзадачей.

4. Если перетащить подзадачу на простую, простая станет Эпиком.
5. Если перетащить подзадачу на подзадачу, то вторая станет Эпиком и покинет свой Эпик.
6. Если перетащить подзадачу на Эпик, то подзадача сменит Эпик.
7. Если перетащить подзадачу на свободное поле, она станет простой задачей.

8. Если перетащить Эпик на простую, Эпик отдаст все свои подзадачи и станет простым. 
9. Если перетащить Эпик на Эпик, первый отдаст все свои подзадачи и станет простым.
10. Если перетащить Эпик на подзадачу, Эпик передаст все свои подзадачи и станет простым, подзадача же станет Эпиком. 