<?php

\DB::query()->from('users')->rightJoin('customers', '<caret>');
