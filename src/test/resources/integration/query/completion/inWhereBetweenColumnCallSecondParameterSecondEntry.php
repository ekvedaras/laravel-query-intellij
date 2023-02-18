<?php

DB::query()->from('users')->whereBetweenColumns('users.id', ['users.email', '<caret>']);
