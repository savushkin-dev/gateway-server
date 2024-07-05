# Описание проекта

## Описание архитектуры

``` mermaid
---
title: Схема взаимодействия NAS - Savushkin
---

%%{ init: {'theme': 'neutral'} }%%

classDiagram
    direction LR

    class NAS_web_server{
        HostToNas()
    }

namespace Savushkin{

    class Savushkin_web_server{
        NasToHost()
        HostToNas()
    }

    class Реализация{
    }

    class DB["БД SQL Server naswms"]{
    }

    class Kafka{
    }

    class ElasticSearch{
    }

    class Gateway_IHS{
    }

    class Gateway_Terminal{
    }

    class Gateway_ERP{
    }

    class Gateway_Prommark{
    }
}

NAS_web_server <|--|> Savushkin_web_server : Туннель, Http, REST
Savushkin_web_server <|-- DB
Savushkin_web_server <|-- Реализация
Savushkin_web_server --|> Kafka

Kafka --|> ElasticSearch
Kafka <|--|> Gateway_IHS
Kafka <|--|> Gateway_Terminal
Kafka <|--|> Gateway_ERP
Kafka <|--|> Gateway_Prommark

style DB fill:#cdd3ca,stroke:#333,stroke-width:4px

```

Сервер **NAS** вызывает на нашем сервере метод **NASToHost**, мы вызываем на их сервере метод **HostToNAS**.
**Реализация** (или любое другое ПО) вызывает на нашем сервере метод **HostToNAS**, который передает запрос на сервер **NAS**.
Все запросы фиксируются в **БД SQL Server**, а также в логах **ElasticSearch**.
**Реализация** читает запросы из **БД**, выполняет и вызывает **HostToNAS** для ответа, делая пометку в **БД** о выполнении.
В **БД** две таблицы: **BD_N2H** и **BD_H2N** для входящих и исходящих запросов.

## Описание данных

Структура таблиц:

![image](https://github.com/kostomarovvv/Bereza-Gateway/assets/17332806/f486fd6e-d28c-4374-afe5-f794bbf55b6a)

Заголовок XML-сообщения:

![image](https://github.com/kostomarovvv/Bereza-Gateway/assets/17332806/0ff5df39-6191-4ea2-a306-f3d9b62482be)

## Описание взаимодействия

При вызове методов **NASToHost** и **HostToNAS** на сервере **Савушкин продукт** выполняется следующая последовательность действий:

1) Проверка правильности заголовка;
2) Сохранение в **БД**;
3) Сохранение в **Kafka** + **ElasticSearch**;
4) Возврат статуса ответа.

## Справочник групп сообщений NS_GRNMSG

| Код поля  | Наименование  | Тип       | Ключ  |
| ---       | ---           | ---       | ---   |
| KGR       | Код группы    | char(10)  | *     |
| NAME      | Наименование  | char(50)  |       |

## Справочник сообщений NS_NMSG

| Код поля      | Наименование  | Тип       | Ключ  |
| ---           | ---           | ---       | ---   |
| KGR           | Код группы    | char(10)  |       |
| KNM           | Код сообщения | char(20)  | *     |
| MSGTYPE       | Наименование  | char(50)  |       |
| DESCRIPTION   | Описание      | char(254) |       |

## Справочник структур сообщений NS_NNODE

| Код поля      | Наименование      | Тип       | Ключ  |
| ---           | ---               | ---       | ---   |
| KNM           | Код сообщения     | char(20)  | *     |
| ORDER         | Порядковый номер  | integer   |       |
| PARENT        | Родитель          | char(50)  |       |
| NODE          | Узел              | char(50)  |       |
| DESCRIPTION   | Наименование      | char(254) |       |
| TYPE          | Тип данных        | char(20)  |       |
| OBLIGATORY    | Обязательность    | char(10)  |       |

## Справочник правил NS_NRULE

| Код поля  | Наименование      | Тип       | Ключ  |
| ---       | ---               | ---       | ---   |
| KNM       | Код сообщения     | char(20)  | *     |
| FACILITY  | Объект            | char(20)  |       |
| ACTION    | Действие          | char(10)  |       |
| SENDER    | Отправитель       | char(10)  |       |
| RECEIVER  | Получатель        | char(10)  |       |
| ROAMING   | Скрипт перевода   | Data      |       |
