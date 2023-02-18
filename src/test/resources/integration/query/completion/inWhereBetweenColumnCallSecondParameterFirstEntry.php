<?php

DB::query()->from('users')->whereBetweenColumns('users.id', ['<caret>']);
