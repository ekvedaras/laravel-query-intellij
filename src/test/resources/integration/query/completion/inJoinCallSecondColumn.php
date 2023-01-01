<?php

\DB::query()->from('users')->join('customers', 'users.id', '<caret>');
