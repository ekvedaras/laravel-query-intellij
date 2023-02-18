<?php

DB::query()->from('users')->whereBetweenColumns('users.id', ['users.email', 'users.first_name', '<caret>']);
